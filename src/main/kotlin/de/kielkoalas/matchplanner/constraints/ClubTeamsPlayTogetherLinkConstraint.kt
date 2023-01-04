package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether
import de.kielkoalas.matchplanner.variables.GroupAssignment

class ClubTeamsPlayTogetherLinkConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val mTeam = problem.teams.first { it.clubs.intersect(wTeam.clubs).isNotEmpty() }
            for ((matchDay, groupNo) in problem.getAllGroups("w")) {
                val wTeamAssignment = GroupAssignment.get(solver, wTeam.competition, matchDay, groupNo, wTeam)
                val mTeamAssignment = GroupAssignment.get(solver, mTeam.competition, matchDay, groupNo, mTeam)
                val travelTogether = ClubTeamsPlayTogether.get(solver, matchDay, groupNo, club)
                val key = "playTogetherLink-${matchDay.number}-$groupNo-${club.abbreviation}"
                val constraint = solver.makeConstraint(-1.0, 0.0, key)
                constraint.setCoefficient(travelTogether, 2.0)
                constraint.setCoefficient(wTeamAssignment, -1.0)
                constraint.setCoefficient(mTeamAssignment, -1.0)
            }
        }
    }
}
