package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.Host

/**
 * A club's home matches should be spread out across the season
 */
class NoBackToBackHomeMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        val threshold = 2
        val matchDays = problem.matchDays.sortedBy { it.number }
        for (team in problem.teams.filter { it.clubs.size == 1 }) {
            for (matchDaySets in matchDays.windowed(threshold, 1)) {
                val key = "no-back-to-back-home-match-${team.abbreviation}-${team.competition}-${matchDaySets.map { it.number }.joinToString("-")}"
                val hostVars = matchDaySets.flatMap { matchDay ->
                    matchDay.getGroupNumbers(team.competition).map { groupNo ->
                        Host.get(solver, matchDay, groupNo, team)
                    }
                }
                solver.buildSumConstraint(0.0, 1.0,  key, hostVars)
            }
        }
    }
}
