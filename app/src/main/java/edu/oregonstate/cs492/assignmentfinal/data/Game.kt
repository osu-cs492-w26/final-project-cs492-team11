package edu.oregonstate.cs492.assignmentfinal.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Game(
    val name: String, // Name of Game
    val metacritic: Int, // Metacritic Score
    val released: String, // Release date in YYYY/MM/DD format
    val website: String, // Website of game
    val developers: List<Developer>, // List of all developers on the game
    val genres: List<Genre>, // List of genres the game is a part of
    // Commiting tags (some random tags the game has) but I can add it if you want
    val publishers: List<Publisher>,
    val esrbRating: ESRBRating, // ESRB Rating (age rating)
)

@JsonClass(generateAdapter = true)
data class Developer(
    val name: String, // Name of developer
    val gamesCount: Int // Number of games developer has made
)

@JsonClass(generateAdapter = true)
data class Genre(
    val name: String // Name of Genre
)

@JsonClass(generateAdapter = true)
data class Publisher(
    val name: String, // Name of publisher
    val gamesCount: Int // Number of games publisher has made
)

@JsonClass(generateAdapter = true)
data class ESRBRating(
    val name: String // "Name" of ESRB Rating
)

