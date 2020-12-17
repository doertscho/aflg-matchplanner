package de.kielkoalas.matchplanner

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.models.Group
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.Solution
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.Location

class Solver {

    companion object {
        init {
            Loader.loadNativeLibraries()
        }
    }

    fun solve(problem: Problem): Solution? {
        val mpSolver = buildSolverInstance()
        val constraintSets = resolveConstraints(problem)
        setupSolver(problem, constraintSets, mpSolver)
        setupObjective(mpSolver, problem)

        val success = doCalculation(mpSolver)
        if (!success) {
            println("No solution was found!")
            return null
        }

        val solution = buildSolution(problem, mpSolver)

        verifySolution(constraintSets, solution)

        return solution
    }

    private fun buildSolverInstance(): MPSolver {
        return MPSolver("bo-afl", MPSolver.OptimizationProblemType.BOP_INTEGER_PROGRAMMING)
    }

    private fun resolveConstraints(problem: Problem): Collection<ConstraintSet> {
        return problem.constraints.map { key ->
            requireNotNull(ConstraintResolver.resolve(key, problem)) {
                "Could not resolve constraint '$key'"
            }
        }
    }

    private fun setupSolver(problem: Problem, constraintSets: Collection<ConstraintSet>, solver: MPSolver) {
        val variableSets = listOf(GroupAssignment, Host, Duel, Location)
        withStopWatch("Setup time") {
            for (variableSet in variableSets) {
                variableSet.createInSolver(problem, solver)
            }

            for (constraintSet in constraintSets) {
                constraintSet.createInSolver(solver)
                println("Setup complete for constraint set ${constraintSet.javaClass.simpleName}")
            }
        }

        println("Number of variables: ${solver.numVariables()}")
        println("Number of constraints: ${solver.numConstraints()}")
    }

    private fun setupObjective(solver: MPSolver, problem: Problem) {
        //
    }

    private fun doCalculation(solver: MPSolver): Boolean {
        solver.setTimeLimit(60_000)
        val result = withStopWatch("Calculation time") {
            solver.solve()
        }
        println("Solver result status: $result")
        return result == MPSolver.ResultStatus.OPTIMAL
    }

    private fun buildSolution(problem: Problem, mpSolver: MPSolver): Solution {
        val matchDayAssignments = problem.matchDays.map { matchDay ->
            val groups: Set<Group> = matchDay.getGroupNumbers(problem).map { groupNo ->
                val host = problem.clubs.find { club ->
                    isSet(Host.get(mpSolver, matchDay, groupNo, club))
                } ?: throw IllegalStateException("No host in ${matchDay.number}:$groupNo")
                val clubs = problem.clubs.filter { club ->
                    isSet(GroupAssignment.get(mpSolver, matchDay, groupNo, club))
                }
                Group(host, clubs.toSet())
            }.toSet()
            matchDay to groups
        }.toMap()
        return Solution(problem, matchDayAssignments)
    }

    private fun isSet(variable: MPVariable): Boolean = variable.solutionValue() == 1.0

    private fun verifySolution(constraintSets: Collection<ConstraintSet>, solution: Solution) {
        withStopWatch("Verification time") {
            for (constraintSet in constraintSets) {
                constraintSet.verify(solution)
                println("Verification successful for constraint set ${constraintSet.javaClass.simpleName}")
            }
        }
    }
}
