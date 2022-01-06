package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class OneShortMatchAgainstEachClubConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((team1, team2) in problem.getDuels("m")) {
            val key = "oneShortMatch-m-${team1.abbreviation}-vs-${team2.abbreviation}"
            val duelVariables = problem.getAllGroups("m")
                .filter { (matchDay, _) -> matchDay.specByCompetition["m"]?.groupSize == 3 }
                .map { (matchDay, groupNo) ->
                    Duel.get(solver, matchDay, groupNo, team1, team2)
                }
            solver.buildExactlyOneConstraint(key, duelVariables)
        }
    }
}
