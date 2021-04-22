package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildAtMostOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host

/**
 * Each group should have one host club.
 */
class OneHostPerGroupConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            val key = "oneHost-${matchDay.number}-$groupNo-m"
            val hostConstraints = problem.clubs.map { club ->
                Host.get(solver, matchDay, groupNo, club, "m") // TODO: dynamicalise
            }
            solver.buildExactlyOneConstraint(key, hostConstraints)
        }
    }
}
