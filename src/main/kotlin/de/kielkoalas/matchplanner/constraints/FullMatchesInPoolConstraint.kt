package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Duel

class FullMatchesInPoolConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for (club1 in problem.clubs) {
            val competition = "m"

            val clubsInPool = problem.pools.find { it.clubs.contains(club1) }?.clubs?.minus(club1)
                ?: error("Club ${club1.name} not found in any pool")

            val team1 = problem.teams.find { it.clubs.contains(club1) && it.competition == competition }
                ?: error("could not find team for club $club1")

            for (club2 in clubsInPool) {
                val team2 = problem.teams.find { it.clubs.contains(club2) && it.competition == competition }
                    ?: error("could not find team for club $club2")

                val key = "fullMatch-$competition-${club1.abbreviation}-vs-${club2.abbreviation}"
                val duelVariables = problem.getAllGroups(competition)
                    .filter { (matchDay, _) -> matchDay.specByCompetition[competition]?.groupSize == 2 }
                    .map { (matchDay, groupNo) ->
                        Duel.get(solver, matchDay, groupNo, team1, team2)
                    }
                solver.buildExactlyOneConstraint(key, duelVariables)
            }
        }
    }
}
