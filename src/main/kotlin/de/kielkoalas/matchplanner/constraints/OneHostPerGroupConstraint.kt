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
        for (competition in problem.competitions) {
            for ((matchDay, groupNo) in problem.getAllGroups(competition)) {
                val key = "oneHost-${matchDay.number}-$groupNo-$competition"
                val hostConstraints = problem.teams.filter { it.competition == competition }.map { team ->
                    Host.get(solver, matchDay, groupNo, team)
                }
                if (competition == "m") {
                    solver.buildExactlyOneConstraint(key, hostConstraints)
                } else {
                    solver.buildAtMostOneConstraint(key, hostConstraints)
                }
            }
        }
    }
}
