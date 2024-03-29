package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class ClubTeamsPlayTogetherKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val club: Club,
    val hostClub: Club
)

/**
 * A set of variables C where C(m, n, c, h) = 1 iff both teams of club c play in
 * group n on match day m at host club h, 0 otherwise.
 */
object ClubTeamsPlayTogether : VariableSet<ClubTeamsPlayTogetherKey> {

    override fun getKey(components: ClubTeamsPlayTogetherKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val club = components.club
        val hostClub = components.hostClub
        return "${matchDay.number}:${club.abbreviation}-teams-play-together-${groupNo}@${hostClub.abbreviation}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (club in problem.clubs.filter { club ->
            val teams = problem.teams.filter { it.clubs.contains(club) }
            teams.size > 1
        }) {
            for ((matchDay, groupNo) in problem.getAllGroups("m")) {
                for (hostClub in problem.clubs) {
                    val key = getKey(ClubTeamsPlayTogetherKey(matchDay, groupNo, club, hostClub))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, club: Club, hostClub: Club): MPVariable {
        val key = getKey(ClubTeamsPlayTogetherKey(matchDay, groupNo, club, hostClub))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
