package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class TwoMatchesAgainstEachClubConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        val competition = "w"
        for ((team1, team2) in problem.getDuels(competition)) {
            val key = "twoMatches-$competition-${team1.abbreviation}-vs-${team2.abbreviation}"
            val duelVariables = problem.getAllGroups(competition)
                .map { (matchDay, groupNo) ->
                    Duel.get(solver, matchDay, groupNo, team1, team2)
                }
            solver.buildSumConstraint(2.0, 2.0, key, duelVariables)
        }
    }
}
