package de.kielkoalas.matchplanner

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable

fun <R> withStopWatch(name: String, action: () -> R): R {
    val stopWatchStart = System.nanoTime()
    val result = action()
    val stopWatchEnd = System.nanoTime()
    val timeNanos = stopWatchEnd - stopWatchStart
    val timeSeconds = timeNanos.toDouble() / 1_000_000_000.0
    println("$name: %.9f".format(timeSeconds))
    return result
}

fun MPSolver.buildImplicationConstraint(
    key: String,
    variableA: MPVariable,
    variableB: MPVariable) {

    val constraint = makeConstraint(-1.0, 0.0, key)
    constraint.setCoefficient(variableA, 1.0)
    constraint.setCoefficient(variableB, -1.0)
}

fun MPSolver.buildEquivalenceConstraint(
    key: String,
    variableA: MPVariable,
    variableB: MPVariable) {

    buildImplicationConstraint("${key}-a", variableA, variableB)
    buildImplicationConstraint("${key}-b", variableB, variableA)
}

fun MPSolver.buildExactlyOneConstraint(key: String, variables: Iterable<MPVariable>) {
    buildSumConstraint(1.0, 1.0, key, variables)
}

fun MPSolver.buildAtLeastOneConstraint(key: String, variables: Iterable<MPVariable>) {
    buildSumConstraint(1.0, Double.POSITIVE_INFINITY, key, variables)
}

fun MPSolver.buildAtMostOneConstraint(key: String, variables: Iterable<MPVariable>) {
    buildSumConstraint(0.0, 1.0, key, variables)
}

fun MPSolver.buildSumConstraint(
    lb: Double,
    ub: Double,
    key: String,
    variables: Iterable<MPVariable>) {

    val constraint = makeConstraint(lb, ub, key)
    variables.forEach { constraint.setCoefficient(it, 1.0) }
}