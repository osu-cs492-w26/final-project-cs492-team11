package edu.oregonstate.cs492.assignmentfinal.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameScreenshots (
    val count: Int = 0, // Number of screenshots
    @Json(name = "results")
    val photos: List<Screenshot> = emptyList() // List of screenshot objects
)

@JsonClass(generateAdapter = true)
data class Screenshot (
    val image: String, // image url
    val hidden: Boolean? = null // I do not know what this is, but I'm adding it in case it's important
)