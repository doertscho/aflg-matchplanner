package de.kielkoalas.matchplanner.variables

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import de.kielkoalas.matchplanner.VariableSet
import de.kielkoalas.matchplanner.models.*

data class GroupAssignmentKey(
    val competition: String,
    val matchDay: MatchDay,
    val groupNo: Int,
    val team: Team,
)

/**
 * A set of variables G where G(m, n, c) = 1 iff club c is part of group n
 * on match day m, 0 otherwise.
 */
object GroupAssignment : VariableSet<GroupAssignmentKey> {

    override fun getKey(components: GroupAssignmentKey): String {
        val competition = components.competition
        val matchDay = components.matchDay
        val groupNo = components.groupNo
        val team = components.team
        return "${matchDay.number}-${competition}:${team.abbreviation}-in-${groupNo}"
    }

    override fun createInSolver(problem: Problem, solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                for (team in problem.teams.filter { it.competition == competition }) {
                    val key = getKey(GroupAssignmentKey(competition, matchDay, groupNo, team))
                    solver.makeBoolVar(key)
                }
            }
        }
    }

    fun get(solver: MPSolver, competition: String, matchDay: MatchDay, groupNo: Int, team: Team): MPVariable {
        val key = getKey(GroupAssignmentKey(competition, matchDay, groupNo, team))
        return solver.lookupVariableOrNull(key)
            ?: throw IllegalStateException("Variable $key has not been created in solver")
    }
}
