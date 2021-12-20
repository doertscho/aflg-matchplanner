package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * Groups should have the correct size, as defined by the match day.
 */
class GroupSizeConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                val key = "groupSize-${matchDay.number}-$groupNo-$competition"
                val groupVariables = problem.teams.filter { it.competition == competition }.map { team ->
                    GroupAssignment.get(solver, competition, matchDay, groupNo, team)
                }
                val size = matchDay.specByCompetition[competition]?.groupSize ?: 2
                if (size == 3) {
                    solver.buildSumConstraint(3.0, 3.0, key, groupVariables)
                } else {
                    solver.buildSumConstraint(0.0, size.toDouble(), key, groupVariables)
                }
            }
        }
    }
}
