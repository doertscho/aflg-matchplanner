package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host
import kotlin.math.ceil
import kotlin.math.floor

/**
 * All clubs should have some home matches and some away matches.
 */
class HomeAwayMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (club in problem.clubs) {
            for (team in club.teams) {

                val balance = if (team == "m") 3.0 else 2.0 // TODO: calculate
                val lb = 2.0
                val ub = ceil(balance + 1.0)

                val key = "homeMatches-${club.abbreviation}-$team"
                val hostVariables = problem.getAllGroups().map { (matchDay, groupNo) ->
                    Host.get(solver, matchDay, groupNo, club, team)
                }
                solver.buildSumConstraint(lb, ub, key, hostVariables)
            }
        }
    }
}
