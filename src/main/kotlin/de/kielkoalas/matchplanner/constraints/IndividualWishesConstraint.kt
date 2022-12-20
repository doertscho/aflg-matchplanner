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
        val six = problem.matchDays.find { it.number == 6 } ?: error("")
        val dresdenSixHostVars =
            (six.getGroupNumbers(dw.competition)).map { groupNo ->
                Host.get(solver, six, groupNo, dw)
            }
        solver.buildSumConstraint(0.0, 0.0, "dresden-away-round-6", dresdenSixHostVars)

        // give a bye for the champions league participants on the first match day
//        val clTeams = problem.teams.filter {
//            (it.abbreviation == "BC" && it.competition == "m")
//                    || (it.abbreviation == "RL" && it.competition == "w")
//        }
//        val first = problem.matchDays.find { it.number == 1 } ?: error("")
//        val clGroupVars = clTeams.flatMap { team ->
//            first.getGroupNumbers(team.competition).map { groupNo ->
//                GroupAssignment.get(solver, team.competition, first, groupNo, team)
//            }
//        }
//        solver.buildSumConstraint(0.0, 0.0, "cl-byes", clGroupVars)
//
//        // stuttgart and munich want an away match in hamburg
//        val hamburg = problem.clubs.find { it.abbreviation == "HD" } ?: error("")
//        val stuttgart = problem.teams.find { it.abbreviation == "ZG" && it.competition == "m" } ?: error("")
//        val munich = problem.teams.find { it.abbreviation == "MK" && it.competition == "m" } ?: error("")
//
//        val stuttgartInHamburgVars = problem.getAllGroups("m").map { (matchDay, groupNo) ->
//            Location.get(solver, matchDay, groupNo, hamburg, stuttgart)
//        }
//        solver.buildExactlyOneConstraint("stuttgart-wants-to-play-in-hamburg", stuttgartInHamburgVars)
//        val munichInHamburgVars = problem.getAllGroups("m").map { (matchDay, groupNo) ->
//            Location.get(solver, matchDay, groupNo, hamburg, munich)
//        }
//        solver.buildExactlyOneConstraint("munich-wants-to-play-in-hamburg", munichInHamburgVars)
//
//        // dresden would like to have a bye in round 9
//        val dresden = problem.teams.find { it.abbreviation == "DW" } ?: error("")
//        val nine = problem.matchDays.find { it.number == 9 } ?: error("")
//        val ddGroupVars =
//            (nine.getGroupNumbers(dresden.competition)).map { groupNo ->
//                GroupAssignment.get(solver, dresden.competition, nine, groupNo, dresden)
//            }
//        solver.buildSumConstraint(0.0, 0.0, "dd-bye", ddGroupVars)
//
    }
}
