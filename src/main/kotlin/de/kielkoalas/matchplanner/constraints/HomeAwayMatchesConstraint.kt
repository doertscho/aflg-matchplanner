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
        for (team in problem.teams) {
            if (team.competition == "m") {
                val balance = 2.0
                val lb = balance - 0.0
                val ub = balance + 1.0

                val keyTriples = "homeMatches-${team.abbreviation}-triples"
                val hostVariablesTriples = problem.getAllGroups("m").mapNotNull { (matchDay, groupNo) ->
                    if (matchDay.specByCompetition["m"]?.groupSize == 3)
                        Host.get(solver, matchDay, groupNo, team)
                    else null
                }
                solver.buildSumConstraint(lb, ub, keyTriples, hostVariablesTriples)

                val duelsKey = "homeMatches-${team.abbreviation}-duels"
                val hostVariablesDuels = problem.getAllGroups("m").mapNotNull { (matchDay, groupNo) ->
                    if (matchDay.specByCompetition["m"]?.groupSize == 2)
                        Host.get(solver, matchDay, groupNo, team)
                    else null
                }
                solver.buildSumConstraint(lb, ub, duelsKey, hostVariablesDuels)

            } else {
                val womenLB = 3.0
                val womenUB = 6.0
                val womenKey = "homeMatches-${team.abbreviation}-women"
                val hostVariablesWomen = problem.getAllGroups("w").map { (matchDay, groupNo) ->
                    Host.get(solver, matchDay, groupNo, team)
                }
                solver.buildSumConstraint(womenLB, womenUB, womenKey, hostVariablesWomen)
            }
        }
    }
}
