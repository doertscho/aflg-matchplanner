package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host

class ClubTeamsHostTogetherConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        // TODO: dynamicalise
        for ((matchDay, groupNo) in problem.getAllGroups()) {
            for (club in problem.clubs) {
                if (club.teams.size == 1) continue
                val key = "hostTogether-${matchDay.number}-$groupNo-${club.abbreviation}"
                val wHostVariable = Host.get(solver, matchDay, groupNo, club, "w")
                val mHostVariable = Host.get(solver, matchDay, groupNo, club, "m")
                solver.buildImplicationConstraint(key, wHostVariable, mHostVariable)
            }
        }
    }
}