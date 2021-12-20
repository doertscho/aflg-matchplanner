package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getOthers
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * The host of a group should not be the only team assigned in a group.
 */
class HostToHaveGuestsConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for (team in problem.teams.filter { it.competition == competition }) {
                    val key = "hostToHaveGuests-${matchDay.number}-$groupNo-${team.abbreviation}-$competition"
                    val groupSize = matchDay.specByCompetition[competition]?.groupSize ?: 2
                    val lb = -1.0 * groupSize
                    val constraint = solver.makeConstraint(lb, 0.0, key)
                    val hostVariable = Host.get(solver, matchDay, groupNo, team)
                    constraint.setCoefficient(hostVariable, 1.0)
                    val others = problem.getOthers(team)
                    for (other in others) {
                        val groupVariable = GroupAssignment.get(solver, competition, matchDay, groupNo, other)
                        constraint.setCoefficient(groupVariable, -1.0)
                    }
                }
            }
        }
    }
}
