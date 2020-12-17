package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * Groups should have the correct size, as defined by the match day.
 */
class GroupSizeConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            val key = "groupSize-${matchDay.number}-$groupNo"
            val groupVariables = problem.clubs.map { club ->
                GroupAssignment.get(solver, matchDay, groupNo, club)
            }
            solver.buildSumConstraint(0.0, matchDay.groupSize.toDouble(), key, groupVariables)
        }
    }
}
