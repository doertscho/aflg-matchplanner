package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.Solution
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host

/**
 * All clubs should have some home matches and some away matches.
 */
class HomeAwayMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (team in problem.teams) {
            if (team.competition == "m") {
                val keyTriples = "homeMatches-${team.abbreviation}-triples"
                val hostVariablesTriples = problem.getAllGroups("m").mapNotNull { (matchDay, groupNo) ->
                    if (matchDay.specByCompetition["m"]?.groupSize == 3)
                        Host.get(solver, matchDay, groupNo, team)
                    else null
                }
                solver.buildSumConstraint(1.0, 2.0, keyTriples, hostVariablesTriples)

                val duelsKey = "homeMatches-${team.abbreviation}-duels"
                val hostVariablesDuels = problem.getAllGroups("m").mapNotNull { (matchDay, groupNo) ->
                    if (matchDay.specByCompetition["m"]?.groupSize == 2)
                        Host.get(solver, matchDay, groupNo, team)
                    else null
                }
                solver.buildSumConstraint(2.0, 2.0, duelsKey, hostVariablesDuels)

            } else {
                val womenKey = "homeMatches-${team.abbreviation}-women"
                val hostVariablesWomen = problem.getAllGroups("w").map { (matchDay, groupNo) ->
                    Host.get(solver, matchDay, groupNo, team)
                }
                solver.buildSumConstraint(2.0, 6.0, womenKey, hostVariablesWomen)
            }
        }
    }
}
