package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host

/**
 * All clubs should have some home matches and some away matches.
 */
class HomeAwayMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (club in problem.clubs) {

            val balance = 2.0
            val lb = balance - 0.0
            val ub = balance + 1.0

            val keyTriples = "homeMatches-${club.abbreviation}-triples"
            val hostVariablesTriples = problem.getAllGroups().mapNotNull { (matchDay, groupNo) ->
                if (matchDay.groupSize == 3)
                    Host.get(solver, matchDay, groupNo, club, "m")
                else null
            }
            solver.buildSumConstraint(lb, ub, keyTriples, hostVariablesTriples)

            val duelsKey = "homeMatches-${club.abbreviation}-duels"
            val hostVariablesDuels = problem.getAllGroups().mapNotNull { (matchDay, groupNo) ->
                if (matchDay.groupSize == 2)
                    Host.get(solver, matchDay, groupNo, club, "m")
                else null
            }
            solver.buildSumConstraint(lb, ub, duelsKey, hostVariablesDuels)

            if (club.teams.contains("w")) {
                val womenLB = 1.0
                val womenUB = 2.0
                val womenKey = "homeMatches-${club.abbreviation}-women"
                val hostVariablesWomen = problem.getAllGroups().map { (matchDay, groupNo) ->
                    Host.get(solver, matchDay, groupNo, club, "w")
                }
                solver.buildSumConstraint(womenLB, womenUB, womenKey, hostVariablesWomen)
            }
        }
    }
}
