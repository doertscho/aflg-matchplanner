package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildEquivalenceConstraint
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getDuels
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.Host

class ClubTeamsPlayTogetherConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups("w")) {
            for ((team1, team2) in problem.getDuels("w")) {
                val key = "playTogether-${matchDay.number}-$groupNo-${team1.abbreviation}-vs-${team2.abbreviation}"
                val constraint = solver.makeConstraint(-5.0, 0.0, key)
                val wDuelVariable = Duel.get(solver, matchDay, groupNo, team1, team2)
                constraint.setCoefficient(wDuelVariable, 1.0)
                for ((mensTeam1, mensTeam2) in problem.getDuels("m")) {
                    if (team1.clubs.intersect(mensTeam1.clubs).isEmpty()
                        || team2.clubs.intersect(mensTeam2.clubs).isEmpty()) {
                        continue
                    }
                    val mDuelVariable = Duel.get(solver, matchDay, groupNo, mensTeam1, mensTeam2)
                    constraint.setCoefficient(mDuelVariable, -1.0)
                }
            }
        }
    }
}
