package de.kielkoalas.matchplanner

import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.Duel
import de.kielkoalas.matchplanner.variables.GroupAssignment
import de.kielkoalas.matchplanner.variables.Host
import de.kielkoalas.matchplanner.variables.Location
import java.time.LocalDate

val KK = Club("KK", "Kiel Koalas", setOf("m"))
val HD = Club("HD", "Hamburg Dockers", setOf("m", "w"))
val BC = Club("BC", "Berlin Crocodiles", setOf("m", "w"))
val RL = Club("RL", "Rheinland Lions", setOf("m", "w"))
val DW = Club("DW", "Dresden Wolves", setOf("m"))
val FR = Club("FR", "Frankfurt Redbacks", setOf("m"))
val HK = Club("HK", "Heidelberg Knights", setOf("m", "w"))
val ZG = Club("ZG", "Zuffenhausen Giants", setOf("m"))
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
    MatchDay(1, mapOf("m" to MatchDataSpec(3), "w" to MatchDataSpec(2))),
    MatchDay(2, mapOf("m" to MatchDataSpec(3), "w" to MatchDataSpec(2))),
    MatchDay(3, mapOf("m" to MatchDataSpec(3), "w" to MatchDataSpec(2))),
    MatchDay(4, mapOf("m" to MatchDataSpec(3), "w" to MatchDataSpec(2))),
    MatchDay(5, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2))
    ),
    MatchDay(6, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2))
    ),
    MatchDay(7, mapOf(
        "m" to MatchDataSpec(3, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),

    MatchDay(8, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),
    MatchDay(9, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),
    MatchDay(10, mapOf(
        "m" to MatchDataSpec(3, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),
    MatchDay(11, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),
    MatchDay(12, mapOf(
        "m" to MatchDataSpec(2, round = 2),
        "w" to MatchDataSpec(2, round = 2))
    ),
)

val poolA = Pool("Pool A", setOf(HD, BC, MK), "m")
val poolB = Pool("Pool B", setOf(DW, RL, FR), "m")
val poolC = Pool("Pool C", setOf(KK, HK, ZG), "m")

val clubs = setOf(KK, HD, BC, RL, FR, HK, ZG, MK, DW)
val mensTeams = clubs.map { club ->
    Team(club.abbreviation, club.name, "m", setOf(club) )
}.toSet()
val womensTeams = setOf(HD, BC, RL, HK).map { club ->
    Team(club.abbreviation, club.name, "w", setOf(club) )
} + setOf(
    Team("AF", name = "All-German Flamingos", competition = "w", clubs = setOf(KK, MK, FR))
)

fun main() {

    val problem = Problem(
        startDate = LocalDate.parse("2022-04-23"),
        clubs = clubs,
        teams = mensTeams + womensTeams,
        pools = setOf(poolA, poolB, poolC),
        distances = distances,
        matchDays = matchDays,
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
            Dictionary.FULL_MATCHES_IN_POOL.name,
            Dictionary.FULL_MATCHES_OUTSIDE_POOL.name,
            Dictionary.NO_CONSECUTIVE_BYES.name,
            Dictionary.CLUB_TEAMS_PLAY_TOGETHER.name,
            Dictionary.CLUB_TEAMS_HOST_TOGETHER.name,
            Dictionary.INDIVIDUAL_WISHES.name,
        )
    )

    val solution = Solver().solve(problem)

    println(solution)
}