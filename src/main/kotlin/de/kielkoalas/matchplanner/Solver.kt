package de.kielkoalas.matchplanner

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.models.Group
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.Solution
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

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
        withStopWatch("Setup time") {
            for (variableSet in problem.variables) {
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
        solver.setTimeLimit(60L * 60_000)
        val timer = Timer()
        var time = 0;
        timer.scheduleAtFixedRate(1000L, 1000L) {
            time += 1;
            print(".")
            if (time % 5 == 0) println(" $time seconds")
        }
        val result = withStopWatch("Calculation time") {
            solver.solve()
        }
        timer.cancel()
        println("Solver result status: $result")
        return result == MPSolver.ResultStatus.OPTIMAL
    }

    private fun buildSolution(problem: Problem, mpSolver: MPSolver): Solution {
        val matchDayAssignments = problem.matchDays.associateWith { matchDay ->
            val groups = problem.competitions.flatMap { competition ->
                matchDay.getGroupNumbers(competition).mapNotNull { groupNo ->
                    problem.teams.find { team ->
                        team.competition == competition &&
                        isSet(Host.get(mpSolver, matchDay, groupNo, team))
                    }?.let { host ->
                        val hostClub = if (host.clubs.size == 1) host.clubs.first() else {
                            host.clubs.find { hostClub ->
                                isSet(JointTeamHost.get(mpSolver, matchDay, groupNo, host, hostClub))
                            } ?: error("No host club defined for ${host.abbreviation} on ${matchDay.number}")
                        }
                        val teams = problem.teams.filter { team ->
                            team.competition == competition &&
                            isSet(GroupAssignment.get(mpSolver, team.competition, matchDay, groupNo, team))
                        }
                        val playingTeams = teams.filter { team ->
                            teams.any { it != team && it.competition == team.competition }
                        }
                        println("group for ${matchDay.number}-$groupNo-$competition: ${host.abbreviation} hosts ${playingTeams.map { "${it.abbreviation}-${it.competition}" }}")
                        Group(hostClub, playingTeams.toSet())
                    }
                }
            }.toSet()
            groups.groupBy { it.host }.values.mapNotNull { sameHostGroups ->
                val host = sameHostGroups.firstOrNull()?.host
                host?.let {
                    Group(host, sameHostGroups.flatMap { it.teams }.toSet())
                }
            }.toSet()
        }
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
