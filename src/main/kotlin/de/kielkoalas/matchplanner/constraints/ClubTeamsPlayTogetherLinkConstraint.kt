package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.ClubTeamsPlayTogether
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Location

class ClubTeamsPlayTogetherLinkConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (wTeam in problem.teams.filter { it.competition == "w" }) {
            val club = wTeam.clubs.first()
            val mTeam = problem.teams.first { it.clubs.intersect(wTeam.clubs).isNotEmpty() }
            for ((matchDay, groupNo) in problem.getAllGroups("w")) {
                for (hostClub in problem.clubs) {
                    val wTeamLocation = Location.get(solver, matchDay, groupNo, hostClub, wTeam)
                    val mTeamLocation = Location.get(solver, matchDay, groupNo, hostClub, mTeam)
                    val travelTogether = ClubTeamsPlayTogether.get(solver, matchDay, groupNo, club, hostClub)
                    val key = "playTogetherLink-${matchDay.number}-$groupNo-${club.abbreviation}-${hostClub.abbreviation}"
                    val constraint = solver.makeConstraint(-1.0, 0.0, key)
                    constraint.setCoefficient(travelTogether, 2.0)
                    constraint.setCoefficient(wTeamLocation, -1.0)
                    constraint.setCoefficient(mTeamLocation, -1.0)
                }
            }
        }
    }
}
