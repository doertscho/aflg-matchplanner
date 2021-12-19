package de.kielkoalas.matchplanner.models

data class Team(
    val abbreviation: String,
    val name: String,
    val team: String,
    val clubs: Set<Club>,
)
