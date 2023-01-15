package de.kielkoalas.matchplanner

import de.kielkoalas.matchplanner.models.*
import de.kielkoalas.matchplanner.variables.*
import java.io.File
import java.time.LocalDate

val KK = Club("KK", "Kiel Koalas", setOf("m"))
val HD = Club("HD", "Hamburg Dockers", setOf("m", "w"))
val BC = Club("BC", "Berlin Crocodiles", setOf("m", "w"))
val RL = Club("RL", "Rheinland Lions", setOf("m", "w"))
val DW = Club("DW", "Dresden Wolves", setOf("m"))
val FR = Club("FR", "Frankfurt Redbacks", setOf("m"))
val HK = Club("HK", "Heidelberg Knights", setOf("m", "w"))
val ZG = Club("SG", "Stuttgart Giants", setOf("m"))
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
    MatchDay(1, mapOf("m" to MatchDataSpec(3, numberOfGroups = 2, byes = 2), "w" to MatchDataSpec(2, numberOfGroups = 2))),
    MatchDay(2, mapOf("m" to MatchDataSpec(2, byes = 0), "w" to MatchDataSpec(2))),
    MatchDay(3, mapOf("m" to MatchDataSpec(3, numberOfGroups = 2, byes = 2), "w" to MatchDataSpec(2, numberOfGroups = 2))),
    MatchDay(4, mapOf("m" to MatchDataSpec(2, byes = 0), "w" to MatchDataSpec(2))),
    MatchDay(5, mapOf("m" to MatchDataSpec(3, numberOfGroups = 2, byes = 2), "w" to MatchDataSpec(2, numberOfGroups = 2))),
    MatchDay(6, mapOf("m" to MatchDataSpec(2, byes = 0), "w" to MatchDataSpec(2))),
    MatchDay(7, mapOf("m" to MatchDataSpec(3, numberOfGroups = 2, byes = 2), "w" to MatchDataSpec(2, numberOfGroups = 2))),
    MatchDay(8, mapOf("m" to MatchDataSpec(2, byes = 0), "w" to MatchDataSpec(2))),
)

val poolA = Pool("Pool A", setOf(HD, BC, RL), "m")
val poolB = Pool("Pool B", setOf(ZG, MK, FR), "m")
val poolC = Pool("Pool C", setOf(KK, HK, DW), "m")

val clubs = setOf(KK, HD, BC, RL, HK, ZG, MK, FR, DW)
val redWolves = Team("RW", "Frankfurt/Dresden RedWolves", "m", setOf(FR, DW))
val mensTeams = clubs.minus(setOf(FR, DW)).map { club ->
    Team(club.abbreviation, club.name, "m", setOf(club) )
}.toSet() + setOf(redWolves)
val womensTeams = setOf(HD, BC, RL, HK, MK).map { club ->
    Team(club.abbreviation, club.name, "w", setOf(club) )
}
//+ setOf(
//    Team("FO", name = "Unstoppable Flying Orcas", competition = "w", clubs = setOf(KK, MK, FR))
//)

fun main() {

    val problem = Problem(
        dates = listOf(
            LocalDate.parse("2023-04-15"),
            LocalDate.parse("2023-05-06"),
            LocalDate.parse("2023-06-03"),
            LocalDate.parse("2023-06-17"),
            LocalDate.parse("2023-07-15"),
            LocalDate.parse("2023-07-29"),
            LocalDate.parse("2023-09-02"),
            LocalDate.parse("2023-09-16"),
        ),
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
            JointTeamHost,
            ClubTeamsHostTogether,
            ClubTeamsPlayTogether,
        ),
        constraints = setOf(
            Dictionary.GROUP_SIZE.name,
            Dictionary.ONE_GROUP_PER_CLUB.name,
            Dictionary.ONE_HOST_PER_GROUP.name,
            Dictionary.GROUP_TO_HAVE_HOST.name,
            Dictionary.HOST_IN_GROUP.name,
            Dictionary.HOST_TO_HAVE_GUESTS.name,
            Dictionary.DUEL_IN_GROUP.name,
            Dictionary.ONE_MATCH_AGAINST_EACH.name,
//            Dictionary.ONE_SHORT_MATCH_AGAINST_EACH.name,
//            Dictionary.TWO_MATCHES_AGAINST_EACH.name,
            Dictionary.NO_BACK_TO_BACK_MATCHES.name,
            Dictionary.HOME_AWAY_MATCHES.name,
            Dictionary.LOCATION_HOST_LINK.name,
            Dictionary.EACH_CLUB_HOSTED_AT_MOST_ONCE.name,
            Dictionary.MAX_DISTANCE.name,
//            Dictionary.FULL_MATCHES_IN_POOL.name,
//            Dictionary.FULL_MATCHES_OUTSIDE_POOL.name,
            Dictionary.NO_CONSECUTIVE_BYES.name,
//            Dictionary.CONSECUTIVE_AWAY_MATCHES.name,
            Dictionary.CLUB_TEAMS_PLAY_TOGETHER_LINK.name,
            Dictionary.CLUB_TEAMS_PLAY_TOGETHER_AT_LEAST_X.name,
            Dictionary.CLUB_TEAMS_HOST_TOGETHER_LINK.name,
            Dictionary.CLUB_TEAMS_HOST_TOGETHER.name,
            Dictionary.INDIVIDUAL_WISHES.name,
            Dictionary.JOINT_TEAM_CLUBS_SHARE_HOSTING.name,
            Dictionary.JOINT_TEAM_HOST_LINK.name,
//            Dictionary.BYES_ON_A_MATCH_DAY.name,
            Dictionary.NO_BACK_TO_BACK_HOME_MATCHES.name,
            Dictionary.NUMBER_OF_BYES.name,
        )
    )

    val solution = Solver().solve(problem)

    println(solution)

    if (solution != null) {
        val fileName = "results/season2023/result-${System.currentTimeMillis()}.md"
        File(fileName).writeText(solution.toString())
    }
}