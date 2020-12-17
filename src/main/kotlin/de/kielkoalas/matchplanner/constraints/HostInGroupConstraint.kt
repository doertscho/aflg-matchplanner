package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host

/**
 * The host of a group should be assigned to the group.
 */
class HostInGroupConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (club in problem.clubs) {
                val key = "hostInGroup-${matchDay.number}-$groupNo-${club.abbreviation}"
                val hostVariable = Host.get(solver, matchDay, groupNo, club)
                val groupVariable = GroupAssignment.get(solver, matchDay, groupNo, club)
                solver.buildImplicationConstraint(key, hostVariable, groupVariable)
            }
        }
    }
}
