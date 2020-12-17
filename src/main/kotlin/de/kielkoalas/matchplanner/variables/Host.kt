package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.Club
import de.kielkoalas.matchplanner.models.MatchDay
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups

/**
 * A set of variables H where H(m, n, c) = 1 iff club c is host of group n
 * on match day m, 0 otherwise.
 */
object Host : VariableSet<Triple<MatchDay, Int, Club>> {

    override fun getKey(components: Triple<MatchDay, Int, Club>): String {
        val matchDay = components.first
        val groupNo = components.second
        val club = components.third
        return "${matchDay.number}:${club.abbreviation}-hosts-group-${groupNo}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (club in problem.clubs) {
                val key = getKey(Triple(matchDay, groupNo, club))
                solver.makeBoolVar(key)
            }
        }
    }

    fun get(solver: MPSolver, matchDay: MatchDay, groupNo: Int, club: Club): MPVariable {
        val key = getKey(Triple(matchDay, groupNo, club))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
