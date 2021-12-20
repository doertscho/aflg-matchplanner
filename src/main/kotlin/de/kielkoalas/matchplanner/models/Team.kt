package de.kielkoalas.matchplanner.models

data class Team(
    val abbreviation: String,
    val name: String,
    val competition: String,
    val clubs: Set<Club>,
)
