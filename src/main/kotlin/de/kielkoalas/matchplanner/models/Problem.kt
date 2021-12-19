package de.kielkoalas.matchplanner.models

import de.kielkoalas.matchplanner.VariableSet
import java.time.LocalDate

data class Problem(
    val clubs: Set<Club>,
    val matchDays: List<MatchDay>,
    val distances: Map<Pair<Club, Club>, Distance>,
    val pools: Set<Pool> = setOf(),
    val teams: Set<Team> = setOf(),
    val competitions: Set<String> = teams.map { it.team }.distinct().toSet(),

    val startDate: LocalDate,

    val variables: Set<VariableSet<*>>,
    val constraints: Set<String>,
)

fun Problem.getTeams(): Set<String> = clubs.flatMap { it.teams }.distinct().toSet()

fun Problem.getOthers(club: Club, team: String) =
    clubs.filter { it != club && it.teams.contains(team) }

fun Problem.getDuels(): List<Triple<Club, Club, Set<String>>> {
    val sorted = clubs.sortedBy { it.name }
    return sorted.flatMapIndexed { index: Int, club1: Club ->
        sorted.drop(index + 1).map { club2: Club ->
            val teams = club1.teams.intersect(club2.teams)
            Triple(club1, club2, teams)
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
