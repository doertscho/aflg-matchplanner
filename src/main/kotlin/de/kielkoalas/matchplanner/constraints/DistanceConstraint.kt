package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDistance
import de.kielkoalas.matchplanner.variables.Location

/**
 * Each club should only travel a certain distance.
 */
class DistanceConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (guest in problem.clubs) {
            for (team in guest.teams) {
                val ub = 60 * (if (team == "m") 56.0 else 28.0)
                val key = "maxDistance-${guest.abbreviation}-$team"
                val constraint = solver.makeConstraint(0.0, ub, key)
                for ((matchDay, groupNo) in problem.getAllGroups()) {
                    for (host in problem.clubs) {
                        if (host != guest && host.teams.contains(team)) {
                            val distance = 2.0 * problem.getDistance(guest, host).minutesByCar
                            val locationVariable = Location.get(solver, matchDay, groupNo, host, guest, "m")
                            constraint.setCoefficient(locationVariable, distance)
                        }
                    }
                }
            }
        }
    }
}
