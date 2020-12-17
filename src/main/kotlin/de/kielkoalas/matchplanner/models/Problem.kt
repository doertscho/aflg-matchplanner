package de.kielkoalas.matchplanner.models

import de.kielkoalas.matchplanner.VariableSet

data class Problem(
    val clubs: Set<Club>,
    val matchDays: List<MatchDay>,
    val distances: Map<Pair<Club, Club>, Distance>,

    val variables: Set<VariableSet<*>>,
    val constraints: Set<String>,
)

fun Problem.getDuels(): List<Pair<Club, Club>> {
    val sorted = clubs.sortedBy { it.name }
    return sorted.flatMapIndexed { index: Int, club1: Club ->
        sorted.drop(index + 1).map { club2: Club ->
            Pair(club1, club2)
        }
    }
}

fun Problem.getAllGroups(): List<Pair<MatchDay, Int>> {
    return matchDays.flatMap { matchDay ->
        matchDay.getGroupNumbers(this).map { Pair(matchDay, it) }
    }
}

fun Problem.getDistance(club1: Club, club2: Club): Distance {
    if (club1 == club2) return Distance(0)
    return distances[Pair(club1, club2)]
        ?: distances[Pair(club2, club1)]
        ?: throw IllegalStateException(
            "No distance defined for clubs ${club1.abbreviation} and ${club2.abbreviation}")
}
