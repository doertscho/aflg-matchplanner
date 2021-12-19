package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * A club should never have two bye rounds directly following one another.
 */
class NoConsecutiveByesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

        for (club in problem.clubs) {
            for (team in club.teams) {
                val threshold = if (team == "m") 2 else 4
                val matchDays = problem.matchDays.filter {
                    (it.groupSize * it.getNumberOfGroups(problem)) < problem.clubs.size
                }.sortedBy { it.number }

                for (matchDaySets in matchDays.windowed(threshold, 1)) {
                    val key = "no-consecutive-byes-${club.abbreviation}-${team}-${matchDaySets.map { it.number }.joinToString("-")}"
                    val assignmentVars = matchDaySets.flatMap { matchDay ->
                        matchDay.getGroupNumbers(problem).map { groupNo ->
                            GroupAssignment.get(solver, matchDay, groupNo, club)
                        }
                    }
                    solver.buildSumConstraint(1.0, threshold.toDouble(), key, assignmentVars)
                }
            }
        }

        for ((club1, club2, teams) in problem.getDuels()) {
            // TODO: See other
            val team = teams.first()
            for ((matchDay, groupNo) in problem.getAllGroups()) {
                val key = "duelInGroup-${matchDay.number}-$groupNo-" +
                        "${club1.abbreviation}-vs-${club2.abbreviation}"
                val duelVariable = Duel.get(solver, matchDay, groupNo, club1, club2, team)
                val groupVariable1 = GroupAssignment.get(solver, matchDay, groupNo, club1)
                val groupVariable2 = GroupAssignment.get(solver, matchDay, groupNo, club2)
                val constraint = solver.makeConstraint(-1.0, 0.0, key)
                constraint.setCoefficient(duelVariable, 2.0)
                constraint.setCoefficient(groupVariable1, -1.0)
                constraint.setCoefficient(groupVariable2, -1.0)
            }
        }
    }
}
