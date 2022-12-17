package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.GroupAssignment

/**
 * Number of teams with a bye round should be as specified.
 */
class ByesOnAMatchDayConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (competition in problem.competitions) {
            val teams = problem.teams.filter { it.competition == competition }
            for (matchDay in problem.matchDays) {
                val spec = matchDay.specByCompetition[competition] ?: error("no spec for comp $competition")
                val key = "byes-${matchDay.number}-$competition"
                val groupVariables = matchDay.getGroupNumbers(competition).flatMap { groupNo ->
                    teams.map { team ->
                        GroupAssignment.get(solver, competition, matchDay, groupNo, team)
                    }
                }
                val activeTeams = (teams.size - spec.byes).toDouble()
                solver.buildSumConstraint(activeTeams, activeTeams, key, groupVariables)
            }
        }
    }
}
