package de.kielkoalas.matchplanner.models

data class Pool(
    val name: String,
    val clubs: Set<Club>,
    val competition: String,
)
