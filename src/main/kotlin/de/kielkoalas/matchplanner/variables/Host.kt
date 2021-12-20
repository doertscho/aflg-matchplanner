package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class HostKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val team: Team,
)

/**
 * A set of variables H where H(m, n, c, t) = 1 iff team t of club c is host of
 * group n on match day m, 0 otherwise.
 */
object Host : VariableSet<HostKey> {

    override fun getKey(components: HostKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val team = components.team
        return "${matchDay.number}-${team.competition}:${team.abbreviation}-hosts-group-${groupNo}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for (team in problem.teams.filter { it.competition == competition }) {
                    val key = getKey(HostKey(matchDay, groupNo, team))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, team: Team): MPVariable {
        val key = getKey(HostKey(matchDay, groupNo, team))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
