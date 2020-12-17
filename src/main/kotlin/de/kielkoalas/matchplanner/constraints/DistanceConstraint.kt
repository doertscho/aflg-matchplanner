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

    private val ub = 27.0 * 60

    override fun createInSolver(solver: MPSolver) {
        for (guest in problem.clubs) {
            val key = "maxDistance-${guest.abbreviation}"
            val constraint = solver.makeConstraint(0.0, ub, key)
            for ((matchDay, groupNo) in problem.getAllGroups()) {
                for (host in problem.clubs) {
                    val distance = 2.0 * problem.getDistance(guest, host).minutesByCar
                    val locationVariable = Location.get(solver, matchDay, groupNo, host, guest)
                    constraint.setCoefficient(locationVariable, distance)
                }
            }
        }
    }
}
