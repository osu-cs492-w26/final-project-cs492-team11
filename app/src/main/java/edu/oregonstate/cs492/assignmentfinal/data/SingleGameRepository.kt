package edu.oregonstate.cs492.assignmentfinal.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

class SingleGameRepository (
    private val service: RAWGService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // Slug is RAWGs version of a readable id
    private var currentSlug: String? = null
    private var cachedGame: Game? = null

    private val cacheMaxAge = 5.minutes
    private val timeSource = TimeSource.Monotonic
    private var timeStamp = timeSource.markNow()

    suspend fun loadSingleGame(
        slug: String,
        key: String
    ) : Result<Game?> {
        return if (shouldFetch(slug)) {
            try {
                val response = service.loadGame(slug, key)
                if (response.isSuccessful) {
                    cachedGame = response.body()
                    timeStamp = timeSource.markNow()
                    currentSlug = slug
                    Result.success(cachedGame)
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        } else {
            Result.success(cachedGame!!)
        }
    }

    private fun shouldFetch(slug: String): Boolean =
        cachedGame == null || currentSlug != slug || (timeStamp + cacheMaxAge).hasPassedNow()
}