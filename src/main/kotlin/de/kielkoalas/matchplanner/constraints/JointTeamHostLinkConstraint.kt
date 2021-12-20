package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.JointTeamHost

class JointTeamHostLinkConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (team in problem.teams.filter { it.competition == "w" && it.clubs.size > 1 }) {
            for (hostClub in team.clubs) {
                val mensTeam = problem.teams.find { it.abbreviation == hostClub.abbreviation }
                    ?: error("did not find mens team for club ${hostClub.abbreviation}")
                for ((matchDay, groupNo) in problem.getAllGroups("w")) {

                    val mensHostVar = Host.get(solver, matchDay, groupNo, mensTeam)
                    val teamHostVar = Host.get(solver, matchDay, groupNo, team)
                    val jointTeamHostVar = JointTeamHost.get(solver, matchDay, groupNo, team, hostClub)

                    val key = "jointTeam-${team.abbreviation}-plays-at-${hostClub.abbreviation}-in-$groupNo@${matchDay.number}"

                    val constraint = solver.makeConstraint(-1.0, 0.0, key)
                    constraint.setCoefficient(jointTeamHostVar, 2.0)
                    constraint.setCoefficient(mensHostVar, -1.0)
                    constraint.setCoefficient(teamHostVar, -1.0)
                }
            }
        }
    }
}
