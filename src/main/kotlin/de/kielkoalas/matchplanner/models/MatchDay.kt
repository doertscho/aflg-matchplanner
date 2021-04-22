package de.kielkoalas.matchplanner.models

data class MatchDay(
    val number: Int,
    val groupSize: Int,
    val numberOfGroups: Int? = null,
    val round: Int = 1,
)

fun MatchDay.getNumberOfGroups(problem: Problem) =
    numberOfGroups ?: problem.clubs.size / groupSize

fun MatchDay.getGroupNumbers(problem: Problem) =
    1 .. getNumberOfGroups(problem)