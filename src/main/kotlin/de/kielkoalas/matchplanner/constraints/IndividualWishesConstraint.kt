package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.buildSumConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.models.getGroupNumbers
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.GroupAssignmentKey
import de.kielkoalas.matchplanner.variables.Location

class IndividualWishesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

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

        val kielTeams = problem.teams.filter { it.abbreviation == "KK" }
        val kielerWoche = problem.matchDays.find { it.number == 6 } ?: error("")
        val groupVars = kielTeams.flatMap { team ->
            (kielerWoche.getGroupNumbers(team.competition)).map { groupNo ->
                GroupAssignment.get(solver, team.competition, kielerWoche, groupNo, team)
            }
        }
        solver.buildSumConstraint(0.0, 0.0, "kiwo", groupVars)
    }
}