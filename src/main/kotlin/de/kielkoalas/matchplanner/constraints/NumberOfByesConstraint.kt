package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * All clubs should have the same number of byes.
 */
class NumberOfByesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (team in problem.teams) {
            val lb = if (team.competition == "m") 7.0 else 6.0
            val ub = if (team.competition == "m") 7.0 else 6.0
            val keyTriples = "assigned-${team.abbreviation}-${team.competition}"
            val assignmentVariables = problem.getAllGroups(team.competition).map { (matchDay, groupNo) ->
                GroupAssignment.get(solver, team.competition, matchDay, groupNo, team)
            }
            solver.buildSumConstraint(lb, ub, keyTriples, assignmentVariables)
        }
    }
}
