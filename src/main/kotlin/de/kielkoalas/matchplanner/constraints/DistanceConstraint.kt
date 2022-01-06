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
        for (competition in problem.competitions) {
            val ub = 60 * (if (competition == "m") 50.0 else 42.0)
            for (guest in problem.teams.filter { it.competition == competition }) {
                if (guest.clubs.size > 1) continue
                val guestClub = guest.clubs.first()
                val key = "maxDistance-${guest.abbreviation}-${guest.competition}"
                val constraint = solver.makeConstraint(0.0, ub, key)
                for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                    for (host in problem.teams.filter { it.competition == competition }.flatMap { it.clubs }) {
                        if (host !in guest.clubs) {
                            val distance = 2.0 * problem.getDistance(guestClub, host).minutesByCar
                            val locationVariable = Location.get(solver, matchDay, groupNo, host, guest)
                            constraint.setCoefficient(locationVariable, distance)
                        }
                    }
                }
            }
        }
    }
}
