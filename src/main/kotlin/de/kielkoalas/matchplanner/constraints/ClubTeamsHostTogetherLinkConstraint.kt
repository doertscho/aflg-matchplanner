package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsHostTogether
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

class ClubTeamsHostTogetherLinkConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val mTeam = problem.teams.first { it.clubs.intersect(wTeam.clubs).isNotEmpty() }
            for ((matchDay, groupNo) in problem.getAllGroups("w")) {
                val wTeamHost = Host.get(solver, matchDay, groupNo, wTeam)
                val mTeamHost = Host.get(solver, matchDay, groupNo, mTeam)
                val hostTogether = ClubTeamsHostTogether.get(solver, matchDay, groupNo, club)
                val key = "hostTogetherLink-${matchDay.number}-$groupNo-${club.abbreviation}"
                val constraint = solver.makeConstraint(-1.0, 0.0, key)
                constraint.setCoefficient(hostTogether, 2.0)
                constraint.setCoefficient(wTeamHost, -1.0)
                constraint.setCoefficient(mTeamHost, -1.0)
            }
        }
    }
}
