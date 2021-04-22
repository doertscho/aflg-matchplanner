package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildAtMostOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Location

class EachClubHostedAtMostOnceConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        val duels = problem.getDuels()
        val flippedDuels = duels.map { (club1, club2, teams) -> Triple(club2, club1, teams) }
        for ((host, guest, teams) in (duels + flippedDuels)) {
            // TODO: This assumes they always play together anyway.
            //  However that's strictly speaking a different constraint.
            val team = teams.first()

            val key = "hostedOnce-${guest.abbreviation}-$team-@-${host.abbreviation}"
            val locationVariables = problem.getAllGroups()
                .map { (matchDay, groupNo) ->
                    Location.get(solver, matchDay, groupNo, host, guest, team)
                }
            solver.buildAtMostOneConstraint(key, locationVariables)
        }
    }
}
