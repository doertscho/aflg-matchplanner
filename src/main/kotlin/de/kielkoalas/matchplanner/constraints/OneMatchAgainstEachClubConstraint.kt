package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class OneMatchAgainstEachClubConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((club1, club2) in problem.getDuels()) {
            val key = "oneMatch-${club1.abbreviation}-vs-${club2.abbreviation}"
            val duelVariables = problem.getAllGroups().map { (matchDay, groupNo) ->
                Duel.get(solver, matchDay, groupNo, club1, club2)
            }
            solver.buildExactlyOneConstraint(key, duelVariables)
        }
    }
}
