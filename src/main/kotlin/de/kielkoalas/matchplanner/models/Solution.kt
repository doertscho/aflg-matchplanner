package de.kielkoalas.matchplanner.models

import java.time.format.DateTimeFormatter

data class Solution(
    val problem: Problem,
    val matchDayAssignments: Map<MatchDay, Set<Group>>
) {
    override fun toString() =
        "match days:\n" + matchDaysToString() + "\n" +
        "by club:\n" + allTeamMatchesToString() + "\n\n" +
        "total travel: ${totalTravelTimeHours()}h\n" +
        "${travelSummary()}\n"
}

fun Solution.findMatchDaysAndGroupsForDuel(team1: Team, team2: Team): List<Pair<MatchDay, Group>> {
    val matches = matchDayAssignments.entries.mapNotNull { (matchDay, groups) ->
        groups.find { it.teams.contains(team1) && it.teams.contains(team2) }
            ?.let { group -> Pair(matchDay, group) }
    }
    if (matches.isEmpty()) {
        throw IllegalStateException("No match ${team1.abbreviation} vs ${team2.abbreviation}")
    }
    return matches
}

fun Solution.matchDaysToString(): String {
    val matchDays = matchDayAssignments.map { (matchDay, groups) ->
        val date = problem.startDate.plusWeeks(2L * (matchDay.number - 1))
        val groupsFormatted = groups.mapNotNull { group ->
            if (group.teams.size > 1) {
                val pairs = group.teams.flatMap { team1 ->
                    group.teams.filter { it != team1 && it.competition == team1.competition }.map { team2 ->
                        setOf(team1, team2)
                    }
                }.distinct().map { it.toList() }
                val matches = pairs.joinToString("\n") { (team1, team2) ->
                    val (o1, o2) = if (team2.clubs.contains(group.host)) Pair(team2, team1) else Pair(team1, team2)
                    "${o1.name} (${o1.competition}) - ${o2.name} (${o2.competition})"
                }
                "Host: ${group.host.name}\n$matches"
            } else {
                null
            }
        }.joinToString("\n\n")
        val byes = problem.teams.filterNot { team -> groups.any { it.teams.contains(team) } }
        val byesFormatted = byes.joinToString(", ") { "${it.name} (${it.competition})" }
        "Match day ${matchDay.number} (${date.format(DateTimeFormatter.ISO_DATE)}):\n\n" +
                "$groupsFormatted\n\n" +
                "Byes: $byesFormatted\n\n" +
                "**************"
    }
    return matchDays.joinToString("\n\n")
}

fun getHomeAway(group: Group, team: Team, other: Team) =
    when (group.host) {
        in team.clubs -> "h"
        in other.clubs -> "a"
        else -> "n"
    }

fun Solution.travelTimeMinutes(team: Team): Int {
    val others = problem.getOthers(team)
    val distances = matchDayAssignments.values.flatten()
        .filter { mda -> mda.teams.contains(team) && others.any { mda.teams.contains(it) } }
        .map { team.clubs.map { club -> problem.getDistance(club, it.host) } }
    return distances.sumBy { ds -> ds.map { d -> d.minutesByCar }.average().toInt() * 2 }
}

fun Solution.totalTravelTimeHours(): Int {
    return problem.teams.sumBy { travelTimeMinutes(it) } / 60
}

fun Solution.travelSummary(): String {
    return problem.competitions.joinToString("\n") { competition ->
        val teams = problem.teams.filter { it.competition == competition }
        val distances = teams
            .map { team -> Pair(team, travelTimeMinutes(team)) }
            .sortedBy { it.second }
            .joinToString(" | ") { (team, distance) ->
                "${team.abbreviation} (${distance / 60}h)"
            }
        "$competition: $distances"
    }
}

fun Solution.summaryForTeam(team: Team): String {
    val travelTime = travelTimeMinutes(team) / 60
    val mda = matchDayAssignments.entries
        .filter { it.value.any { group -> group.teams.contains(team) } }
        .mapNotNull { (matchDay, groups) ->
            val group = groups.find { group -> group.teams.contains(team) }
            if (group == null) {
                null
            } else {
                val isHome = team.clubs.contains(group.host)
                Pair(matchDay, isHome)
            }
        }
    val homeMatches = mda.filter { (_, isHome) -> isHome }.map { (matchDay, _) -> matchDay.number }
    val awayMatches = mda.filterNot { (_, isHome) -> isHome }.map { (matchDay, _) -> matchDay.number }
    return "summary: " +
            "${homeMatches.size} home match days (${homeMatches.joinToString(", ")}), " +
            "${awayMatches.size} away match days (${awayMatches.joinToString(", ")}), " +
            "travel: ${travelTime}h"
}

fun Solution.allTeamMatchesToString(): String {
    return problem.teams.joinToString("\n\n") { team ->
        teamMatchesToString(team)
    }
}

fun Solution.teamMatchesToString(team: Team): String {
    val others = problem.getOthers(team)
    val matchesByTeam = others.joinToString(" | ") { other ->
        val matches = findMatchDaysAndGroupsForDuel(team, other)
            .joinToString(", ") { (matchDay, group) ->
                val homeAway = getHomeAway(group, team, other)
                val host = if (homeAway == "h") {
                    if (team.clubs.size > 1) " @ ${group.host.abbreviation}" else ""
                } else {
                    if (other.clubs.size > 1) " @ ${group.host.abbreviation}" else ""
                }
                "${matchDay.number} ($homeAway$host)"
            }
        "${other.abbreviation} $matches"
    }

    val matchesByRound = matchDayAssignments.map { (matchDay, groups) ->
        val matches = groups.find { it.teams.contains(team) }?.let { group ->
            val opponents = group.teams.filter { it.competition == team.competition && it != team }
            opponents.joinToString(", ") { other ->
                val homeAway = getHomeAway(group, team, other)
                val host = if (homeAway == "h") {
                    if (team.clubs.size > 1) " @ ${group.host.abbreviation}" else ""
                } else {
                    if (other.clubs.size > 1) " @ ${group.host.abbreviation}" else ""
                }
                "${other.abbreviation} ($homeAway$host)"
            }
        } ?: "bye"
        "${matchDay.number}: $matches"
    }.joinToString(" | ")

    val byes = problem.matchDays.map { it.number }.filterNot { number ->
        val regex = " $number \\(".toRegex()
        regex.containsMatchIn(matchesByTeam)
    }.joinToString(", ")

    val summary = summaryForTeam(team)
    return "${team.name} (${team.competition}):\n" +
            "by team: $matchesByTeam; byes: $byes\n" +
            "by round: $matchesByRound\n" +
            summary
}
