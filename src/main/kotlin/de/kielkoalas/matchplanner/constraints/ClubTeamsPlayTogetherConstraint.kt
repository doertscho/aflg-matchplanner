package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildEquivalenceConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class ClubTeamsPlayTogetherConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for ((club1, club2, teams) in problem.getDuels()) {
                if (teams.size == 1) {
                    continue
                }
                if (teams.size > 2) {
                    TODO("more than two teams per club are not yet supported")
                    // but should be easy to do – just form any pair
                }
                val key = "teamsTogether-${matchDay.number}-$groupNo-${club1.abbreviation}-vs-${club2.abbreviation}"
                val (teamKeyA, teamKeyB) = teams.toList()
                val variableA = Duel.get(solver, matchDay, groupNo, club1, club2, teamKeyA)
                val variableB = Duel.get(solver, matchDay, groupNo, club1, club2, teamKeyB)
                solver.buildEquivalenceConstraint(key, variableA, variableB)
            }
        }
    }
}
