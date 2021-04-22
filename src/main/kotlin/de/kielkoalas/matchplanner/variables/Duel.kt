package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class DuelKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val club1: Club,
    val club2: Club,
    val team: String,
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
        val clubs = listOf(components.club1, components.club2).map{ it.name }.sorted()
        val club1 = clubs[0]
        val club2 = clubs[1]
        val team = components.team
        if (club1 == club2) {
            throw IllegalArgumentException("Requesting duel of $club1 against itself")
        }
        return "${matchDay.number}${team}:${club1}-vs-${club2}@$groupNo"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for ((club1, club2, teams) in problem.getDuels()) {
                for (team in teams) {
                    val key = getKey(DuelKey(matchDay, groupNo, club1, club2, team))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(
        solver: MPSolver, matchDay: MatchDay, groupNo: Int, club1: Club, club2: Club, team: String,
    ): MPVariable {
        val key = getKey(DuelKey(matchDay, groupNo, club1, club2, team))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
