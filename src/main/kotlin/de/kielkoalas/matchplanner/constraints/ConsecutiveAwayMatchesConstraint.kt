package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * A club should never have two bye rounds directly following one another.
 */
class ConsecutiveAwayMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            val threshold = 4
            for (team in problem.teams.filter { it.competition == competition }) {
                val matchDays = problem.matchDays.sortedBy { it.number }

                for (matchDaySets in matchDays.windowed(threshold, 1)) {
                    val key = "consecutive-away-${team.abbreviation}-${competition}-${matchDaySets.map { it.number }.joinToString("-")}"
                    val hostVars = matchDaySets.flatMap { matchDay ->
                        matchDay.getGroupNumbers(competition).map { groupNo ->
                            Host.get(solver, matchDay, groupNo, team)
                        }
                    }
                    solver.buildSumConstraint(1.0, threshold.toDouble(), key, hostVars)
                }
            }
        }
    }
}
