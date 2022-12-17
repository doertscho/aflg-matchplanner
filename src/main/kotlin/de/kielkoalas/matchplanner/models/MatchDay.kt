package de.kielkoalas.matchplanner.models

data class MatchDay(
    val number: Int,
    val specByCompetition: Map<String, MatchDataSpec>,
)

data class MatchDataSpec(
    val groupSize: Int,
    val numberOfGroups: Int = 3,
    val round: Int = 1,
    val byes: Int = 0
)

fun MatchDay.hasByes(problem: Problem, competition: String) =
    specByCompetition[competition]?.run {
        val involved = getNumberOfGroups(competition) * groupSize
        val total = problem.teams.filter { it.competition == competition }.size
        (involved < total) || (total % groupSize > 0)
    } ?: error("competition $competition not available")

fun MatchDay.getNumberOfGroups(competition: String) =
    specByCompetition[competition]?.run {
        numberOfGroups
    } ?: error("competition $competition not available")

fun MatchDay.getGroupNumbers(competition: String) =
    1 .. getNumberOfGroups(competition)
