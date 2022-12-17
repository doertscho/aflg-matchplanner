package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildAtLeastOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel

class NumberOfMatchesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            val numberOfMatches = if (competition == "m") 10 else 6
            val rounds = problem.matchDays.mapNotNull { it.specByCompetition[competition]?.round }.distinct()
            for (round in rounds) {
                for ((team1, team2) in problem.getDuels(competition)) {
                    val key = "atLeastOneMatch-$competition-${team1.abbreviation}-vs-${team2.abbreviation}-r$round"
                    val duelVariables = problem.getAllGroups(competition)
                        .filter { (matchDay, _) -> matchDay.specByCompetition[competition]?.round == round }
                        .map { (matchDay, groupNo) ->
                            Duel.get(solver, matchDay, groupNo, team1, team2)
                        }
                    solver.buildAtLeastOneConstraint(key, duelVariables)
                }
            }
        }
    }
}
