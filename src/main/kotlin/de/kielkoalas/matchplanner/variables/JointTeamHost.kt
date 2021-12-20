package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class JointTeamHostKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val team: Team,
    val hostClub: Club,
)

/**
 * A set of variables H where H(m, n, c, t) = 1 iff team t of club c is host of
 * group n on match day m, 0 otherwise.
 */
object JointTeamHost : VariableSet<JointTeamHostKey> {

    override fun getKey(components: JointTeamHostKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val team = components.team
        val hostClub = components.hostClub
        return "${matchDay.number}-${team.competition}:${team.abbreviation}-hosts-group-${groupNo}@${hostClub.abbreviation}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (team in problem.teams.filter { it.clubs.size > 1 }) {
            for ((matchDay, groupNo) in problem.getAllGroups(team.competition)) {
                for (hostClub in team.clubs) {
                    val key = getKey(JointTeamHostKey(matchDay, groupNo, team, hostClub))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, team: Team, hostClub: Club): MPVariable {
        val key = getKey(JointTeamHostKey(matchDay, groupNo, team, hostClub))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
