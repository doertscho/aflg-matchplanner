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
        for (team in problem.teams.filter { it.clubs.size > 1 }) {
            for ((matchDay, groupNo) in problem.getAllGroups(team.competition)) {
                val key = "jointTeam-${team.abbreviation}-has-host-in-$groupNo@${matchDay.number}"
                val constraint = solver.makeConstraint(0.0, 0.0, key)
                val hostVar = Host.get(solver, matchDay, groupNo, team)
                constraint.setCoefficient(hostVar, 1.0)
                for (hostClub in team.clubs) {
                    val jointTeamHostVar = JointTeamHost.get(solver, matchDay, groupNo, team, hostClub)
                    constraint.setCoefficient(jointTeamHostVar, -1.0)
                }
            }
        }
    }
}
