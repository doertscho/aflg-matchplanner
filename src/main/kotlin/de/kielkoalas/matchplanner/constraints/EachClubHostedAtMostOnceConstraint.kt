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
        for (competition in problem.competitions) {
            val duels = problem.getDuels(competition)
            val flippedDuels = duels.map { (team1, team2) -> Pair(team2, team1) }
            for ((host, guest) in (duels + flippedDuels)) {
                val key = "hostedOnce-${guest.abbreviation}-$competition-@-${host.abbreviation}"
                val locationVariables = problem.getAllGroups(competition)
                    .flatMap { (matchDay, groupNo) ->
                        host.clubs.map { hostClub ->
                            Location.get(solver, matchDay, groupNo, hostClub, guest)
                        }
                    }
                solver.buildAtMostOneConstraint(key, locationVariables)
            }
        }
    }
}
