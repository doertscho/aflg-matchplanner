package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * A club should never have two bye rounds directly following one another.
 */
class NoConsecutiveByesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            val threshold = if (competition == "m") 2 else 2
            val matchDays = problem.matchDays.filter { it.hasByes(problem, competition) }.sortedBy { it.number }
            for (team in problem.teams.filter { it.competition == competition }) {
                for (matchDaySets in matchDays.windowed(threshold, 1)) {
                    val key = "no-consecutive-byes-${team.abbreviation}-${competition}-${matchDaySets.map { it.number }.joinToString("-")}"
                    val assignmentVars = matchDaySets.flatMap { matchDay ->
                        matchDay.getGroupNumbers(competition).map { groupNo ->
                            GroupAssignment.get(solver, competition, matchDay, groupNo, team)
                        }
                    }
                    solver.buildSumConstraint(1.0, threshold.toDouble(), key, assignmentVars)
                }
            }
        }
    }
}
