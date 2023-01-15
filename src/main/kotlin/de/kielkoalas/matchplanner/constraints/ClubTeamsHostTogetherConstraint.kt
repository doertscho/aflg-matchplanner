package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsHostTogether
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether
import de.kielkoalas.matchplanner.variables.Host

class ClubTeamsHostTogetherConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val key = "hostTogetherAtLeast-${club.abbreviation}"
            val hostTogetherVars = problem.getAllGroups("w").map {(matchDay, groupNo) ->
                ClubTeamsHostTogether.get(solver, matchDay, groupNo, club)
            }
            solver.buildSumConstraint(2.0, 3.0, key, hostTogetherVars)
        }
    }
}
