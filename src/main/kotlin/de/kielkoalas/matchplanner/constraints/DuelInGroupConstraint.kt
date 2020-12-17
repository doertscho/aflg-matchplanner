package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * Two clubs competing in a given group on a given match day should be assigned
 * to this group on that match day.
 */
class DuelInGroupConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((club1, club2) in problem.getDuels()) {
            for ((matchDay, groupNo) in problem.getAllGroups()) {
                val key = "duelInGroup-${matchDay.number}-$groupNo-" +
                        "${club1.abbreviation}-vs-${club2.abbreviation}"
                val duelVariable = Duel.get(solver, matchDay, groupNo, club1, club2)
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
