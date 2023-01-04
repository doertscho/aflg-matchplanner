package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.JointTeamHost

class IndividualWishesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

        // home match for Kiel on Kieler Woche match day
        val kk = problem.teams.find { it.abbreviation == "KK" } ?: error("")
        val four = problem.matchDays.find { it.number == 4 } ?: error("")
        val kiwoMatchVars =
            (four.getGroupNumbers(kk.competition)).map { groupNo ->
                Host.get(solver, four, groupNo, kk)
            }
        solver.buildSumConstraint(1.0, 1.0, "kiel-home-round-4", kiwoMatchVars)

        // Dresden to host on match day 1
        val one = problem.matchDays.find { it.number == 1 } ?: error("")
        val dw = problem.clubs.find { it.abbreviation == "DW" } ?: error("")
        val rw = problem.teams.find { it.abbreviation == "RW" } ?: error("")
        val dresdenOneHostVars = listOf(
            Host.get(solver, one, 1, rw),
            JointTeamHost.get(solver, one, 1, rw, dw)
        )
        solver.buildSumConstraint(2.0, 2.0, "dresden-host-round-1", dresdenOneHostVars)
    }
}
