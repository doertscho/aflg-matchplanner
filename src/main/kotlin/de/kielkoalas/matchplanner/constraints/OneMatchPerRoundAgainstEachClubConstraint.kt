package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class OneMatchPerRoundAgainstEachClubConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        val rounds = problem.matchDays.map { it.round }.distinct()
        for (round in rounds) {
            for ((club1, club2, teams) in problem.getDuels()) {
                for (team in teams) {
                    val key = "oneMatch-$team-${club1.abbreviation}-vs-${club2.abbreviation}-r$round"
                    val duelVariables = problem.getAllGroups()
                        .filter { (matchDay, _) -> matchDay.round == round }
                        .map { (matchDay, groupNo) ->
                            Duel.get(solver, matchDay, groupNo, club1, club2, team)
                        }
                    solver.buildExactlyOneConstraint(key, duelVariables)
                }
            }
        }
    }
}
