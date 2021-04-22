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
                val ub = 60 * if (team == "m") 28.0 else 22.0
                val key = "maxDistance-${guest.abbreviation}"
                val constraint = solver.makeConstraint(0.0, ub, key)
                for ((matchDay, groupNo) in problem.getAllGroups()) {
                    for (host in problem.clubs) {
                        val distance = 2.0 * problem.getDistance(guest, host).minutesByCar
                        val locationVariable = Location.get(solver, matchDay, groupNo, host, guest, team)
                        constraint.setCoefficient(locationVariable, distance)
                    }
                }
            }
        }
    }
}
