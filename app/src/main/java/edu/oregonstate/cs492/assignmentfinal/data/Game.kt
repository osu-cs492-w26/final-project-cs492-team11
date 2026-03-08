package edu.oregonstate.cs492.assignmentfinal.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Game(
    val name: String? = null,
    val metacritic: Int? = null,
    val released: String? = null,
    val website: String? = null,
    val developers: List<Developer>? = null,
    val genres: List<Genre>? = null,
    val publishers: List<Publisher>? = null,
    val esrbRating: ESRBRating? = null,
)

@JsonClass(generateAdapter = true)
data class Developer(
    val name: String? = null,
    val gamesCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class Genre(
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class Publisher(
    val name: String? = null,
    val gamesCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class ESRBRating(
    val name: String? = null
)