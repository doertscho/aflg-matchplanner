package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildAtMostOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * Each club should be assigned to no more than one group on a match day.
 */
class OneGroupPerClubConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            for (matchDay in problem.matchDays) {
                for (team in problem.teams.filter { it.competition == competition }) {
                    val key = "oneGroupPerClub-${matchDay.number}-${team.abbreviation}-$competition"
                    val groupVariables = matchDay.getGroupNumbers(competition).map { groupNo ->
                        GroupAssignment.get(solver, competition, matchDay, groupNo, team)
                    }
                    solver.buildAtMostOneConstraint(key, groupVariables)
                }
            }
        }
    }
}
