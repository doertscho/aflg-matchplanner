package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class LocationKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val hostClub: Club,
    val guestTeam: Team,
)

/**
 * A set of variables L where L(m, n, h, g, t) = 1 iff team t of club g travels
 * to the ground of club h in group n on match day m.
 */
object Location : VariableSet<LocationKey> {

    override fun getKey(components: LocationKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val host = components.hostClub
        val guest = components.guestTeam
        return "${matchDay.number}-${guest.competition}:${host.abbreviation}-hosts-team-${guest.abbreviation}@$groupNo"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for (hostClub in problem.clubs) {
                    for (guestTeam in problem.teams.filter { it.competition == competition }) {
                        val key = getKey(LocationKey(matchDay, groupNo, hostClub, guestTeam))
                        solver.makeBoolVar(key)
                    }
                }
            }
        }
    }

    fun get(
        solver: MPSolver, matchDay: MatchDay, groupNo: Int, hostClub: Club, guestTeam: Team,
    ): MPVariable {
        val key = getKey(LocationKey(matchDay, groupNo, hostClub, guestTeam))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
