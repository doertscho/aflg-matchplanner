package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Duel

class FullMatchesOutsidePoolConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (club1 in problem.clubs) {
            val competition = "m"
            val team1 = problem.teams.find { it.clubs.contains(club1) && it.competition == competition }
                ?: error("could not find team for club $club1")

            val otherPools = problem.pools.filter { !it.clubs.contains(club1) }
            for (pool in otherPools) {
                val key = "fullMatch-$competition-${club1.abbreviation}-vs-${pool.name}"
                val duelVariables = pool.clubs.flatMap { club2 ->
                    val team2 = problem.teams.find { it.clubs.contains(club2) && it.competition == competition }
                        ?: error("could not find team for club $club2")
                    problem.getAllGroups(competition)
                        .filter { (matchDay, _) -> matchDay.specByCompetition[competition]?.groupSize == 2 }
                        .map { (matchDay, groupNo) ->
                            Duel.get(solver, matchDay, groupNo, team1, team2)
                        }
                }
                solver.buildExactlyOneConstraint(key, duelVariables)
            }
        }
    }
}
