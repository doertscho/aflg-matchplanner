package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether

class ClubTeamsPlayTogetherAtLeastXConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val key = "playTogetherAtLeast-${club.abbreviation}"
            val playTogetherVars = problem.getAllGroups("w").flatMap {(matchDay, groupNo) ->
                problem.clubs.map { hostClub ->
                    ClubTeamsPlayTogether.get(solver, matchDay, groupNo, club, hostClub)
                }
            }
            solver.buildSumConstraint(5.0, 7.0, key, playTogetherVars)
        }
    }
}
