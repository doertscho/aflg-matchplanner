package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.JointTeamHost

class JointTeamClubsShareHostingConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (team in problem.teams.filter { it.competition == "w" && it.clubs.size > 1 }) {
            for (hostClub in team.clubs) {
                val key = "jointTeam-${team.abbreviation}-plays-one-match-at-${hostClub.abbreviation}"
                val hostVars = problem.getAllGroups("w").map { (matchDay, groupNo) ->
                    JointTeamHost.get(solver, matchDay, groupNo, team, hostClub)
                }
                solver.buildSumConstraint(1.0, 1.0, key, hostVars)
            }
        }
    }
}
