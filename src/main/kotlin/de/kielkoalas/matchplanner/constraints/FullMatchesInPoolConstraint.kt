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
            val team = "m"

            val clubsInPool = problem.pools.find { it.clubs.contains(club1) }?.clubs?.minus(club1)
                ?: error("Club ${club1.name} not found in any pool")

            for (club2 in clubsInPool) {
                val key = "fullMatch-$team-${club1.abbreviation}-vs-${club2.abbreviation}"
                val duelVariables = problem.getAllGroups()
                    .filter { (matchDay, _) -> matchDay.groupSize == 2 }
                    .map { (matchDay, groupNo) ->
                        Duel.get(solver, matchDay, groupNo, club1, club2, team)
                    }
                solver.buildExactlyOneConstraint(key, duelVariables)
            }
        }
    }
}
