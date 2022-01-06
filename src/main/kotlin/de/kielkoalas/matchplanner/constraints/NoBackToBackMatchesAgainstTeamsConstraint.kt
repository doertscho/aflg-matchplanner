package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * A club shouldn't play the same club on consecutive match days.
 */
class NoBackToBackMatchesAgainstTeamsConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            val threshold = 2
            val matchDays = problem.matchDays.sortedBy { it.number }
            for ((team1, team2) in problem.getDuels(competition)) {
                for (matchDaySets in matchDays.windowed(threshold, 1)) {
                    val key = "no-back-to-back-${team1.abbreviation}vs-${team2.abbreviation}-${competition}-${matchDaySets.map { it.number }.joinToString("-")}"
                    val assignmentVars = matchDaySets.flatMap { matchDay ->
                        matchDay.getGroupNumbers(competition).map { groupNo ->
                            Duel.get(solver, matchDay, groupNo, team1, team2)
                        }
                    }
                    solver.buildSumConstraint(0.0, 1.0,  key, assignmentVars)
                }
            }
        }
    }
}
