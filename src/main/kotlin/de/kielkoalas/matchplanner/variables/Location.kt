package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.Club
import de.kielkoalas.matchplanner.models.MatchDay
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups

data class LocationKey(
    val matchDay: MatchDay,
    val groupNo: Int,
    val hostClub: Club,
    val guestClub: Club,
)

/**
 * A set of variables L where L(m, n, h, g) = 1 iff club g travels to the
 * ground of club h in group n on match day m.
 */
object Location : VariableSet<LocationKey> {

    override fun getKey(components: LocationKey): String {
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val host = components.hostClub
        val guest = components.guestClub
        return "${matchDay.number}:${host}-hosts-club-${guest}@$groupNo"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (host in problem.clubs) {
                for (guest in problem.clubs) {
                    val key = getKey(LocationKey(matchDay, groupNo, host, guest))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(
        solver: MPSolver, matchDay: MatchDay, groupNo: Int, host: Club, guest: Club
    ): MPVariable {
        val key = getKey(LocationKey(matchDay, groupNo, host, guest))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
