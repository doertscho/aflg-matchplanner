package de.kielkoalas.matchplanner.models

data class Group(
    val host: Club,
    val teams: Set<Team>,
)
