package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.Location

class IndividualWishesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

        val fr = problem.teams.find { it.abbreviation == "FR" } ?: error("")

        // stuttgart not to host in round one
        val sg = problem.teams.find { it.abbreviation == "SG" } ?: error("")
        val one = problem.matchDays.find { it.number == 1 } ?: error("")
        val hostVars =
            (one.getGroupNumbers(sg.competition)).map { groupNo ->
                Host.get(solver, one, groupNo, sg)
            }
        solver.buildSumConstraint(0.0, 0.0, "stuttgart-away-round-1", hostVars)

        // avoid clashes with GFL for Kiel and Heidelberg
        val kk = problem.teams.find { it.abbreviation == "KK" } ?: error("")
        val hurricanesHomeMatchDay = problem.matchDays.find { it.number == 4 } ?: error("")
        val kielHostVars =
            (hurricanesHomeMatchDay.getGroupNumbers(kk.competition)).map { groupNo ->
                Host.get(solver, hurricanesHomeMatchDay, groupNo, kk)
            }
        solver.buildSumConstraint(0.0, 0.0, "kiel-away-round-4", kielHostVars)

        // bye for Kiel on Kieler Woche match day
        val five = problem.matchDays.find { it.number == 5 } ?: error("")
        val kiwoMatchVars =
            (five.getGroupNumbers(kk.competition)).map { groupNo ->
                GroupAssignment.get(solver, kk.competition, five, groupNo, kk)
            }
        solver.buildSumConstraint(0.0, 0.0, "kiel-bye-round-5", kiwoMatchVars)

        // Dresden to host on match day 5, but not on 6
        val dw = problem.teams.find { it.abbreviation == "DW" } ?: error("")
        val dresdenFiveHostVars =
            (five.getGroupNumbers(dw.competition)).map { groupNo ->
                Host.get(solver, five, groupNo, dw)
            }
        solver.buildSumConstraint(1.0, 1.0, "dresden-host-round-5", dresdenFiveHostVars)
        val seven = problem.matchDays.find { it.number == 7 } ?: error("")
        val dresdenSixMatchVars =
            (seven.getGroupNumbers(dw.competition)).map { groupNo ->
                GroupAssignment.get(solver, dw.competition, seven, groupNo, dw)
            }
        solver.buildSumConstraint(0.0, 0.0, "dresden-bye-round-7", dresdenSixMatchVars)

        // orcas clubs to play together on one match day
        val orcasDay = problem.matchDays.find { it.number == 1 } ?: error("")
        val mk = problem.teams.find { it.abbreviation == "MK" } ?: error("")
        val orcas = problem.teams.find { it.abbreviation == "FO" } ?: error("")
        val orcasDayVars = listOf(kk, mk, fr, orcas).map { team ->
            GroupAssignment.get(solver, team.competition, orcasDay, 1, team)
        } + listOf(Host.get(solver, orcasDay, 1, fr))
        val allSet = orcasDayVars.size.toDouble()
        solver.buildSumConstraint(allSet, allSet, "orcas-day", orcasDayVars)
    }
}
