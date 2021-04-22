package de.kielkoalas.matchplanner.models

data class Solution(
    val problem: Problem,
    val matchDayAssignments: Map<MatchDay, Set<Group>>
) {
    override fun toString() =
        "match days:\n" + matchDaysToString() + "\n" +
        "by club:\n" + allTeamMatchesToString()
}

fun Solution.findMatchDaysAndGroupsForDuel(club1: Club, club2: Club): List<Pair<MatchDay, Group>> {
    val matches = matchDayAssignments.entries.mapNotNull { (matchDay, groups) ->
        groups.find { it.clubs.contains(club1) && it.clubs.contains(club2) }
            ?.let { group -> Pair(matchDay, group) }
    }
    if (matches.isEmpty()) {
        throw IllegalStateException("No match ${club1.abbreviation} vs ${club2.abbreviation}")
    }
    return matches
}

fun Solution.matchDaysToString(): String {
    val matchDays = matchDayAssignments.map { (matchDay, groups) ->
        val groupsFormatted = groups.mapNotNull { group ->
            if (group.clubs.size > 1) {
                val pairs = group.clubs.flatMap { club1 ->
                    group.clubs.filter { it != club1 }.map { club2 ->
                        setOf(club1, club2)
                    }
                }.distinct().map { it.toList() }
                val matches = pairs.flatMap { (club1, club2) ->
                    val clubs = if (club2 == group.host) Pair(club2, club1) else Pair(club1, club2)
                    val teams = club1.teams.intersect(club2.teams)
                    teams.map { team -> "${clubs.first.name} ($team) - ${clubs.second.name} ($team)" }
                }.joinToString("\n")
                "Host: ${group.host.name}\n$matches"
            } else {
                null
            }
        }.joinToString("\n\n")
        val byes = problem.clubs.filterNot { club -> groups.any { it.clubs.contains(club) } }
        val byesFormatted = if (byes.isEmpty())
            ""
        else
            "Byes: ${byes.joinToString(", ") { it.name }}\n\n"
        "Match day ${matchDay.number}:\n\n$groupsFormatted\n\n$byesFormatted**************"
    }
    return matchDays.joinToString("\n\n")
        // TODO: dynamicalise
        .replace("Zuffenhausen Giants (w)", "Southern Tigeroos (w)")
        .replace("Frankfurt Redbacks (w)", "Frankfurt Redcats (w)")
}

fun getHomeAway(group: Group, club: Club, other: Club) =
    when (group.host) {
        club -> "h"
        other -> "a"
        else -> "n"
    }

fun Solution.summaryForClub(club: Club, team: String): String {
    val others = problem.getOthers(club, team)
    val distances = matchDayAssignments.values.flatten()
        .filter { mda -> mda.clubs.contains(club) && others.any { mda.clubs.contains(it) } }
        .map { problem.getDistance(club, it.host) }
    val travelTime = distances.sumBy { it.minutesByCar * 2 } / 60
    val groups = others.flatMap { other ->
        findMatchDaysAndGroupsForDuel(club, other).map { (_, group) -> group }
    }
    val homeMatches = groups.count { it.host == club }
    val awayMatches = groups.size - homeMatches
    return "[${homeMatches}h, ${awayMatches}a/n, travel: ${travelTime}h]"
}

fun Solution.allTeamMatchesToString(): String {
    return problem.clubs.flatMap { club ->
        club.teams.map { teamMatchesToString(club, it) }
    }.joinToString("\n")
        // TODO: dynamicalise
        .replace("ZG (w)", "ST (w)")
}

fun Solution.teamMatchesToString(club: Club, team: String): String {
    val others = problem.getOthers(club, team)
    val matches = others.joinToString(", ") { other ->
        val matches = findMatchDaysAndGroupsForDuel(club, other)
            .joinToString(", ") { (matchDay, group) ->
                val homeAway = getHomeAway(group, club, other)
                "${matchDay.number}$homeAway"
            }
        "${other.abbreviation} $matches"
    }
    val summary = summaryForClub(club, team)
    return "${club.abbreviation} ($team): $matches $summary"
}
