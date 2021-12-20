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
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for (host in problem.teams.filter { it.competition == competition }) {
                    if (host.clubs.size > 1) continue
                    val hostClub = host.clubs.first()
                    for (guest in problem.teams.filter { it.competition == competition }) {
                        val hostVariable = Host.get(solver, matchDay, groupNo, host)
                        val key = "locationHost-${matchDay.number}-$groupNo-" +
                                "${host.abbreviation}-${guest.abbreviation}-$competition"
                        val groupVariable = GroupAssignment.get(solver, competition, matchDay, groupNo, guest)
                        val locationVariable = Location.get(solver, matchDay, groupNo, hostClub, guest)
                        val constraint = solver.makeConstraint(0.0, 1.0, key)
                        constraint.setCoefficient(hostVariable, 1.0)
                        constraint.setCoefficient(groupVariable, 1.0)
                        constraint.setCoefficient(locationVariable, -2.0)
                    }
                }
            }
        }
    }
}
