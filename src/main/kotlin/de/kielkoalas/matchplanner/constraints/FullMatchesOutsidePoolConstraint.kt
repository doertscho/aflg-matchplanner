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
            val team = "m"

            val otherPools = problem.pools.filter { !it.clubs.contains(club1) }
            for (pool in otherPools) {
                val key = "fullMatch-$team-${club1.abbreviation}-vs-${pool.name}"
                val duelVariables = pool.clubs.flatMap { club2 ->
                    problem.getAllGroups()
                        .filter { (matchDay, _) -> matchDay.groupSize == 2 }
                        .map { (matchDay, groupNo) ->
                            Duel.get(solver, matchDay, groupNo, club1, club2, team)
                        }
                }
                solver.buildExactlyOneConstraint(key, duelVariables)
            }
        }
    }
}
