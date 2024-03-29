package de.kielkoalas.matchplanner

import de.kielkoalas.matchplanner.constraints.*
import de.kielkoalas.matchplanner.models.Problem

typealias ConstraintSetFactory = (Problem) -> ConstraintSet

object ConstraintResolver {

    private val providedImplementations: Map<String, ConstraintSetFactory> = mapOf(
        Dictionary.GROUP_SIZE.name to { p -> GroupSizeConstraint(p) },
        Dictionary.ONE_GROUP_PER_CLUB.name to { p -> OneGroupPerClubConstraint(p) },
        Dictionary.ONE_HOST_PER_GROUP.name to { p -> OneHostPerGroupConstraint(p) },
        Dictionary.GROUP_TO_HAVE_HOST.name to { p -> GroupToHaveHostConstraint(p) },
        Dictionary.HOST_IN_GROUP.name to { p -> HostInGroupConstraint(p) },
        Dictionary.HOST_TO_HAVE_GUESTS.name to { p -> HostToHaveGuestsConstraint(p) },
        Dictionary.ONE_MATCH_PER_ROUND_AGAINST_EACH.name to { p -> OneMatchPerRoundAgainstEachClubConstraint(p) },
        Dictionary.ONE_MATCH_AGAINST_EACH.name to { p -> OneMatchAgainstEachClubConstraint(p) },
        Dictionary.ONE_SHORT_MATCH_AGAINST_EACH.name to { p -> OneShortMatchAgainstEachClubConstraint(p) },
        Dictionary.TWO_MATCHES_AGAINST_EACH.name to { p -> TwoMatchesAgainstEachClubConstraint(p) },
        Dictionary.NO_BACK_TO_BACK_MATCHES.name to { p -> NoBackToBackMatchesAgainstTeamsConstraint(p) },
        Dictionary.DUEL_IN_GROUP.name to { p -> DuelInGroupConstraint(p) },
        Dictionary.HOME_AWAY_MATCHES.name to { p -> HomeAwayMatchesConstraint(p) },
        Dictionary.EACH_CLUB_HOSTED_AT_MOST_ONCE.name to { p -> EachClubHostedAtMostOnceConstraint(p) },
        Dictionary.LOCATION_HOST_LINK.name to { p -> LocationHostConstraint(p) },
        Dictionary.MAX_DISTANCE.name to { p -> DistanceConstraint(p) },
        Dictionary.CLUB_TEAMS_PLAY_TOGETHER_LINK.name to { p -> ClubTeamsPlayTogetherLinkConstraint(p) },
        Dictionary.CLUB_TEAMS_HOST_TOGETHER_LINK.name to { p -> ClubTeamsHostTogetherLinkConstraint(p) },
        Dictionary.CLUB_TEAMS_PLAY_TOGETHER_AT_LEAST_X.name to { p -> ClubTeamsPlayTogetherAtLeastXConstraint(p) },
        Dictionary.CLUB_TEAMS_HOST_TOGETHER.name to { p -> ClubTeamsHostTogetherConstraint(p) },
        Dictionary.FULL_MATCHES_IN_POOL.name to { p -> FullMatchesInPoolConstraint(p) },
        Dictionary.FULL_MATCHES_OUTSIDE_POOL.name to { p -> FullMatchesOutsidePoolConstraint(p) },
        Dictionary.NO_CONSECUTIVE_BYES.name to { p -> NoConsecutiveByesConstraint(p) },
        Dictionary.CONSECUTIVE_AWAY_MATCHES.name to { p -> ConsecutiveAwayMatchesConstraint(p) },
        Dictionary.INDIVIDUAL_WISHES.name to { p -> IndividualWishesConstraint(p) },
        Dictionary.JOINT_TEAM_CLUBS_SHARE_HOSTING.name to { p -> JointTeamClubsShareHostingConstraint(p) },
        Dictionary.JOINT_TEAM_HOST_LINK.name to { p -> JointTeamHostLinkConstraint(p) },
        Dictionary.BYES_ON_A_MATCH_DAY.name to { p -> ByesOnAMatchDayConstraint(p) },
        Dictionary.NO_BACK_TO_BACK_HOME_MATCHES.name to { p -> NoBackToBackHomeMatchesConstraint(p) },
        Dictionary.NUMBER_OF_BYES.name to { p -> NumberOfByesConstraint(p) },
    )

    fun resolve(key: String, problem: Problem): ConstraintSet? {

        val pluginImplementation = resolvePlugin(key)
        if (pluginImplementation != null) {
            return pluginImplementation(problem)
        }

        val providedImplementation = resolveProvided(key)
        if (providedImplementation != null) {
            return providedImplementation(problem)
        }

        return null
    }

    private fun resolveProvided(key: String): ConstraintSetFactory? {
        return providedImplementations[key]
    }

    private fun resolvePlugin(key: String): ConstraintSetFactory? {
        return null
    }
}