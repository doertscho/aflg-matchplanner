package de.kielkoalas.matchplanner.models

data class Solution(
    val problem: Problem,
    val matchDayAssignments: Map<MatchDay, Set<Group>>
) {
    override fun toString() =
        "match days:\n" + matchDaysToString() + "\n" +
        "by club:\n" + clubMatchesToString()
}

fun Solution.findMatchDayAndGroupForDuel(club1: Club, club2: Club): Pair<MatchDay, Group> {
    for (entry in matchDayAssignments.entries) {
        val group = entry.value.find { it.clubs.contains(club1) && it.clubs.contains(club2) }
        if (group != null) {
            return Pair(entry.key, group)
        }
    }
    throw IllegalStateException("No match ${club1.abbreviation} vs ${club2.abbreviation}")
}

fun Solution.matchDaysToString(): String {
    val matches = matchDayAssignments.flatMap { entry ->
        entry.value.map { group ->
            if (group.clubs.size > 1) {
                val guests = group.clubs.joinToString(", ") { it.abbreviation }
                val host = group.host.abbreviation
                "${entry.key.number}: $guests @ $host"
            } else {
                null
            }
        }
    }
    return matches.filterNotNull().joinToString("\n")
}

fun Solution.summaryForClub(club: Club): String {
    val others = problem.clubs.filter { it != club }
    val distances = matchDayAssignments.values.flatten()
        .filter { it.clubs.contains(club) }
        .map { problem.getDistance(club, it.host) }
    val travelTime = distances.sumBy { it.minutesByCar * 2 } / 60
    val groups = others.map { other ->
        findMatchDayAndGroupForDuel(club, other).second
    }
    val homeMatches = groups.count { it.host == club }
    val awayMatches = groups.size - homeMatches
    return "[${homeMatches}h, ${awayMatches}a, travel: ${travelTime}h]"
}

fun Solution.clubMatchesToString(): String {
    return problem.clubs.joinToString("\n") { club ->
        val others = problem.clubs.filter { it != club }
        val matches = others.joinToString(", ") { other ->
            val (matchDay, group) = findMatchDayAndGroupForDuel(club, other)
            val homeAway = if (group.host == club) "h" else "a"
            "${other.abbreviation} ${matchDay.number}$homeAway"
        }
        val summary = summaryForClub(club)
        "${club.abbreviation}: $matches $summary"
    }
}
