package de.kielkoalas.matchplanner.models

import de.kielkoalas.matchplanner.VariableSet
import java.time.LocalDate

data class Problem(
    val clubs: Set<Club>,
    val matchDays: List<MatchDay>,
    val distances: Map<Pair<Club, Club>, Distance>,
    val pools: Set<Pool> = setOf(),
    val teams: Set<Team> = setOf(),
    val competitions: Set<String> = teams.map { it.competition }.distinct().toSet(),

    val startDate: LocalDate,

    val variables: Set<VariableSet<*>>,
    val constraints: Set<String>,
)

fun Problem.getOthers(team: Team) =
    teams.filter { it != team && it.competition == team.competition }

fun Problem.getDuels(competition: String): List<Pair<Team, Team>> {
    val teamsInComp = teams.filter { it.competition == competition }
    return teamsInComp.flatMapIndexed { index: Int, team1: Team ->
        teamsInComp.drop(index + 1).map { team2: Team ->
            Pair(team1, team2)
        }
    }
}

fun Problem.getAllGroups(competition: String): List<Pair<MatchDay, Int>> {
    return matchDays.flatMap { matchDay ->
        matchDay.getGroupNumbers(competition).map { Pair(matchDay, it) }
    }
}

fun Problem.getDistance(club1: Club, club2: Club): Distance {
    if (club1 == club2) return Distance(0)
    return distances[Pair(club1, club2)]
        ?: distances[Pair(club2, club1)]
        ?: throw IllegalStateException(
            "No distance defined for clubs ${club1.abbreviation} and ${club2.abbreviation}")
}
