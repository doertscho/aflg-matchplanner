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
            if (team.competition == "w") {
                val keyTriples = "assigned-${team.abbreviation}"
                val assignmentVariables = problem.getAllGroups("m").map { (matchDay, groupNo) ->
                    GroupAssignment.get(solver, team.competition, matchDay, groupNo, team)
                }
                solver.buildSumConstraint(6.0, 6.0, keyTriples, assignmentVariables)
            }
        }
    }
}
