package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class ClubTeamsHostTogetherKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val club: Club,
)

/**
 * A set of variables C where C(m, n, c) = 1 iff both teams of club c host
 * group n on match day m, 0 otherwise.
 */
object ClubTeamsHostTogether : VariableSet<ClubTeamsHostTogetherKey> {

    override fun getKey(components: ClubTeamsHostTogetherKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val club = components.club
        return "${matchDay.number}:${club.abbreviation}-teams-host-together-${groupNo}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (club in problem.clubs.filter { club ->
            val teams = problem.teams.filter { it.clubs.contains(club) }
            teams.size > 1
        }) {
            for ((matchDay, groupNo) in problem.getAllGroups("m")) {
                val key = getKey(ClubTeamsHostTogetherKey(matchDay, groupNo, club))
                solver.makeBoolVar(key)
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, club: Club): MPVariable {
        val key = getKey(ClubTeamsHostTogetherKey(matchDay, groupNo, club))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
