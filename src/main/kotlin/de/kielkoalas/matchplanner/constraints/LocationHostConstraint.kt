package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.Location

/**
 * The host of a group should be reflected in the Location variable set.
 */
class LocationHostConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (host in problem.clubs) {
                for (guest in problem.clubs) {
                    val team = "m"
                    val hostVariable = Host.get(solver, matchDay, groupNo, host, team)
                    val key = "locationHost-${matchDay.number}-$groupNo-" +
                            "${host.abbreviation}-${guest.abbreviation}-$team"
                    val groupVariable = GroupAssignment.get(solver, matchDay, groupNo, guest)
                    val locationVariable = Location.get(solver, matchDay, groupNo, host, guest, team)
                    val constraint = solver.makeConstraint(0.0, 1.0, key)
                    constraint.setCoefficient(hostVariable, 1.0)
                    constraint.setCoefficient(groupVariable, 1.0)
                    constraint.setCoefficient(locationVariable, -2.0)
                }
            }
        }
    }
}
