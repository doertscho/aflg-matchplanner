package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether
import de.kielkoalas.matchplanner.variables.GroupAssignment

class ClubTeamsPlayTogetherAtLeastXConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val key = "playTogetherAtLeast-${club.abbreviation}"
            val playTogetherVars = problem.getAllGroups("w").map {(matchDay, groupNo) ->
                ClubTeamsPlayTogether.get(solver, matchDay, groupNo, club)
            }
            solver.buildSumConstraint(4.0, 6.0, key, playTogetherVars)
        }
    }
}
