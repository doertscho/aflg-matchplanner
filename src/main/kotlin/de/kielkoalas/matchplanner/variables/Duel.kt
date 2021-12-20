package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class DuelKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val team1: Team,
    val team2: Team,
)

/**
 * A set of variables D where D(m, n, c1, c2, t) = 1 iff team t of club c1
 * plays against team t of club c2 in group n on match day m. To avoid
 * duplication, one variable shall be created per pair of clubs that represents
 * either order.
 */
object Duel : VariableSet<DuelKey> {

    override fun getKey(components: DuelKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val teams = listOf(components.team1, components.team2).map{ "${it.abbreviation}-${it.competition}" }.sorted()
        val team1 = teams[0]
        val team2 = teams[1]
        if (team1 == team2) {
            throw IllegalArgumentException("Requesting duel of $team1 against itself")
        }
        return "${matchDay.number}:${team1}-vs-${team2}@$groupNo"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for ((team1, team2) in problem.getDuels(competition)) {
                    val key = getKey(DuelKey(matchDay, groupNo, team1, team2))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(
        solver: MPSolver, matchDay: MatchDay, groupNo: Int, team1: Team, team2: Team,
    ): MPVariable {
        val key = getKey(DuelKey(matchDay, groupNo, team1, team2))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
