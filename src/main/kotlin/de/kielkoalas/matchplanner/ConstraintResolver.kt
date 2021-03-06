package de.kielkoalas.matchplanner

import de.kielkoalas.matchplanner.constraints.*
import de.kielkoalas.matchplanner.models.Problem

typealias ConstraintSetFactory = (Problem) -> ConstraintSet

object ConstraintResolver {

    private val providedImplementations: Map<String, ConstraintSetFactory> = mapOf(
        Dictionary.GROUP_SIZE.name to { p -> GroupSizeConstraint(p) },
        Dictionary.ONE_GROUP_PER_CLUB.name to { p -> OneGroupPerClubConstraint(p) },
        Dictionary.ONE_HOST_PER_GROUP.name to { p -> OneHostPerGroupConstraint(p) },
        Dictionary.HOST_IN_GROUP.name to { p -> HostInGroupConstraint(p) },
        Dictionary.HOST_TO_HAVE_GUESTS.name to { p -> HostToHaveGuestsConstraint(p) },
        Dictionary.ONE_MATCH_AGAINST_EACH.name to { p -> OneMatchAgainstEachClubConstraint(p) },
        Dictionary.DUEL_IN_GROUP.name to { p -> DuelInGroupConstraint(p) },
        Dictionary.HOME_AWAY_MATCHES.name to { p -> HomeAwayMatchesConstraint(p) },
        Dictionary.LOCATION_HOST_LINK.name to { p -> LocationHostConstraint(p) },
        Dictionary.MAX_DISTANCE.name to { p -> DistanceConstraint(p) },
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