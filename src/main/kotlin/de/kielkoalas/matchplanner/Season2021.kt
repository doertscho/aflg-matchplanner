package de.kielkoalas.matchplanner

import de.kielkoalas.matchplanner.models.Club
import de.kielkoalas.matchplanner.models.Distance
import de.kielkoalas.matchplanner.models.MatchDay
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.Location

val KK = Club("KK", "Kiel Koalas", setOf("m"))
val HD = Club("HD", "Hamburg Dockers", setOf("m", "w"))
val BC = Club("BC", "Berlin Crocodiles", setOf("m", "w"))
val RL = Club("RL", "Rheinland Lions", setOf("m", "w"))
val DW = Club("DW", "Dresden Wolves", setOf("m"))
val FR = Club("FR", "Frankfurt Redbacks", setOf("m", "w"))
val HK = Club("HK", "Heidelberg Knights", setOf("m"))
val ZG = Club("ZG", "Zuffenhausen Giants", setOf("m", "w"))
val MK = Club("MK", "Munich Kangaroos", setOf("m"))

val distances = mapOf(
    Pair(KK, HD) to Distance(60),
    Pair(KK, BC) to Distance(210),
    Pair(KK, RL) to Distance(330),
    Pair(KK, DW) to Distance(320),
    Pair(KK, FR) to Distance(350),
    Pair(KK, HK) to Distance(390),
    Pair(KK, ZG) to Distance(450),
    Pair(KK, MK) to Distance(500),

    Pair(HD, BC) to Distance(170),
    Pair(HD, RL) to Distance(260),
    Pair(HD, DW) to Distance(280),
    Pair(HD, FR) to Distance(300),
    Pair(HD, HK) to Distance(340),
    Pair(HD, ZG) to Distance(400),
    Pair(HD, MK) to Distance(450),

    Pair(BC, RL) to Distance(330),
    Pair(BC, DW) to Distance(180),
    Pair(BC, FR) to Distance(300),
    Pair(BC, HK) to Distance(340),
    Pair(BC, ZG) to Distance(360),
    Pair(BC, MK) to Distance(330),

    Pair(RL, DW) to Distance(330),
    Pair(RL, FR) to Distance(130),
    Pair(RL, HK) to Distance(160),
    Pair(RL, ZG) to Distance(240),
    Pair(RL, MK) to Distance(330),

    Pair(DW, FR) to Distance(250),
    Pair(DW, HK) to Distance(290),
    Pair(DW, ZG) to Distance(290),
    Pair(DW, MK) to Distance(260),

    Pair(FR, HK) to Distance(60),
    Pair(FR, ZG) to Distance(150),
    Pair(FR, MK) to Distance(210),

    Pair(HK, ZG) to Distance(100),
    Pair(HK, MK) to Distance(180),

    Pair(ZG, MK) to Distance(120),
)

val matchDays = listOf(
    MatchDay(1, 3),
    MatchDay(2, 3),
    MatchDay(3, 3),
    MatchDay(4, 2, 3),
    MatchDay(5, 2, 3),
    MatchDay(6, 2, 3)
)
val matchDaysSecondRound = matchDays.map {
    it.copy(number = it.number + matchDays.size, round = 2)
}

fun main() {

    val problem = Problem(
        clubs = setOf(KK, HD, BC, RL, FR, HK, ZG, MK, DW),
        distances = distances,
        matchDays = matchDays,// + matchDaysSecondRound,
        variables = setOf(
            GroupAssignment,
            Host,
            Duel,
            Location,
        ),
        constraints = setOf(
            Dictionary.GROUP_SIZE.name,
            Dictionary.ONE_GROUP_PER_CLUB.name,
            Dictionary.ONE_HOST_PER_GROUP.name,
            Dictionary.HOST_IN_GROUP.name,
            Dictionary.HOST_TO_HAVE_GUESTS.name,
            Dictionary.ONE_MATCH_PER_ROUND_AGAINST_EACH.name,
            Dictionary.DUEL_IN_GROUP.name,
            Dictionary.HOME_AWAY_MATCHES.name,
            Dictionary.LOCATION_HOST_LINK.name,
            Dictionary.EACH_CLUB_HOSTED_AT_MOST_ONCE.name,
            Dictionary.MAX_DISTANCE.name,
            Dictionary.CLUB_TEAMS_PLAY_TOGETHER.name,
            Dictionary.CLUB_TEAMS_HOST_TOGETHER.name,
        )
    )

    val solution = Solver().solve(problem)

    println(solution)
}