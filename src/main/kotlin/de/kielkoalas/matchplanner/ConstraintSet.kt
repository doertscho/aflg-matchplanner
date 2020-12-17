package de.kielkoalas.matchplanner

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.models.Solution

interface ConstraintSet {

    fun createInSolver(solver: MPSolver)

    fun verify(solution: Solution) {
        // verification is optional
    }
}
