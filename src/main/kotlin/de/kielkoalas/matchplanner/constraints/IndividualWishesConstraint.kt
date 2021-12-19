package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildExactlyOneConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Location

class IndividualWishesConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {

        val hamburg = problem.clubs.find { it.abbreviation == "HD" } ?: error("")
        val stuttgart = problem.clubs.find { it.abbreviation == "ZG" } ?: error("")
        val munich = problem.clubs.find { it.abbreviation == "MK" } ?: error("")

        val stuttgartInHamburgVars = problem.getAllGroups().map { (matchDay, groupNo) ->
            Location.get(solver, matchDay, groupNo, hamburg, stuttgart, "m")
        }
        solver.buildExactlyOneConstraint("stuttgart-wants-to-play-in-hamburg", stuttgartInHamburgVars)
        val munichInHamburgVars = problem.getAllGroups().map { (matchDay, groupNo) ->
            Location.get(solver, matchDay, groupNo, hamburg, munich, "m")
        }
        solver.buildExactlyOneConstraint("munich-wants-to-play-in-hamburg", munichInHamburgVars)
    }
}