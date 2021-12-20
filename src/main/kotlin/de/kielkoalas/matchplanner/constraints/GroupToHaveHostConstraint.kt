package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * If a group isn't empty, it should have a host.
 */
class GroupToHaveHostConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                val key = "groupHasHost-${matchDay.number}-$groupNo-$competition"
                val constraint = solver.makeConstraint(-2.0, 0.0, key)
                problem.teams.filter { it.competition == competition }.forEach { team ->
                    val groupVar = GroupAssignment.get(solver, competition, matchDay, groupNo, team)
                    val hostVar = Host.get(solver, matchDay, groupNo, team)
                    constraint.setCoefficient(groupVar, 1.0)
                    constraint.setCoefficient(hostVar, -3.0)
                }
            }
        }
    }
}
