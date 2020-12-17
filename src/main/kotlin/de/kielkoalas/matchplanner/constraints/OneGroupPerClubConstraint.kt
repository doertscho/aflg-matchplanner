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
        for (matchDay in problem.matchDays) {
            for (club in problem.clubs) {
                val key = "oneGroupPerClub-${matchDay.number}-${club.abbreviation}"
                val groupVariables = matchDay.getGroupNumbers(problem).map { groupNo ->
                    GroupAssignment.get(solver, matchDay, groupNo, club)
                }
                solver.buildAtMostOneConstraint(key, groupVariables)
            }
        }
    }
}
