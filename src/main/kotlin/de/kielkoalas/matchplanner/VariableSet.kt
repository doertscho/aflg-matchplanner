package de.kielkoalas.matchplanner

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.models.Problem

interface VariableSet<T> {

    fun getKey(components: T): String

    fun createInSolver(problem: Problem, solver: MPSolver)
}
