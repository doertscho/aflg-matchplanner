package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.Club
import de.kielkoalas.matchplanner.models.MatchDay
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups

data class HostKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val club: Club,
    val team: String,
)

/**
 * A set of variables H where H(m, n, c, t) = 1 iff team t of club c is host of
 * group n on match day m, 0 otherwise.
 */
object Host : VariableSet<HostKey> {

    override fun getKey(components: HostKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val club = components.club
        val team = components.team
        return "${matchDay.number}:${club.abbreviation}-$team-hosts-group-${groupNo}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (club in problem.clubs) {
                for (team in club.teams) {
                    val key = getKey(HostKey(matchDay, groupNo, club, team))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, club: Club, team: String): MPVariable {
        val key = getKey(HostKey(matchDay, groupNo, club, team))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
