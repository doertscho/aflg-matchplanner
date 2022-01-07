package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Location

class IndividualWishesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

        // give a bye for the champions league participants on the first match day
        val clTeams = problem.teams.filter {
            (it.abbreviation == "BC" && it.competition == "m")
                    || (it.abbreviation == "RL" && it.competition == "w")
        }
        val first = problem.matchDays.find { it.number == 1 } ?: error("")
        val clGroupVars = clTeams.flatMap { team ->
            first.getGroupNumbers(team.competition).map { groupNo ->
                GroupAssignment.get(solver, team.competition, first, groupNo, team)
            }
        }
        solver.buildSumConstraint(0.0, 0.0, "cl-byes", clGroupVars)

        // stuttgart and munich want an away match in hamburg
        val hamburg = problem.clubs.find { it.abbreviation == "HD" } ?: error("")
        val stuttgart = problem.teams.find { it.abbreviation == "ZG" && it.competition == "m" } ?: error("")
        val munich = problem.teams.find { it.abbreviation == "MK" && it.competition == "m" } ?: error("")

        val stuttgartInHamburgVars = problem.getAllGroups("m").map { (matchDay, groupNo) ->
            Location.get(solver, matchDay, groupNo, hamburg, stuttgart)
        }
        solver.buildExactlyOneConstraint("stuttgart-wants-to-play-in-hamburg", stuttgartInHamburgVars)
        val munichInHamburgVars = problem.getAllGroups("m").map { (matchDay, groupNo) ->
            Location.get(solver, matchDay, groupNo, hamburg, munich)
        }
        solver.buildExactlyOneConstraint("munich-wants-to-play-in-hamburg", munichInHamburgVars)

        // kiel would like to have a bye in round 6
        val kiel = problem.teams.find { it.abbreviation == "KK" } ?: error("")
        val six = problem.matchDays.find { it.number == 6 } ?: error("")
        val groupVars =
            (six.getGroupNumbers(kiel.competition)).map { groupNo ->
                GroupAssignment.get(solver, kiel.competition, six, groupNo, kiel)
            }
        solver.buildSumConstraint(0.0, 0.0, "kiel-bye", groupVars)

        // dresden would like to have a bye in round 9
        val dresden = problem.teams.find { it.abbreviation == "DW" } ?: error("")
        val nine = problem.matchDays.find { it.number == 9 } ?: error("")
        val ddGroupVars =
            (nine.getGroupNumbers(dresden.competition)).map { groupNo ->
                GroupAssignment.get(solver, dresden.competition, nine, groupNo, dresden)
            }
        solver.buildSumConstraint(0.0, 0.0, "dd-bye", ddGroupVars)
    }
}
