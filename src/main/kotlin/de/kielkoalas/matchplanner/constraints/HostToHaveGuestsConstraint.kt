package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * The host of a group should not be the only club assigned in a group.
 */
class HostToHaveGuestsConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (club in problem.clubs) {
                val key = "hostToHaveGuests-${matchDay.number}-$groupNo-${club.abbreviation}"
                val lb = -1.0 * matchDay.groupSize
                val constraint = solver.makeConstraint(lb, 0.0, key)
                val hostVariable = Host.get(solver, matchDay, groupNo, club)
                constraint.setCoefficient(hostVariable, 1.0)
                val others = problem.clubs.filter { it != club }
                for (other in others) {
                    val groupVariable = GroupAssignment.get(solver, matchDay, groupNo, other)
                    constraint.setCoefficient(groupVariable, -1.0)
                }
            }
        }
    }
}
