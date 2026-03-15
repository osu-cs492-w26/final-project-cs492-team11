package edu.oregonstate.cs492.assignmentfinal.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

class GameScreenshotsRepository (
    private val service: RAWGService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // Slug is RAWGs version of a readable id
    private var currentSlug: String? = null
    private var cachedGameScreenshots: GameScreenshots? = null

    private val cacheMaxAge = 5.minutes
    private val timeSource = TimeSource.Monotonic
    private var timeStamp = timeSource.markNow()

    suspend fun loadGameScreenshots(
        slug: String,
        key: String
    ) : Result<GameScreenshots?> {
        return if (shouldFetch(slug)) {
            try {
                val response = service.loadScreenshots(slug, key)
                if (response.isSuccessful) {
                    cachedGameScreenshots = response.body()
                    timeStamp = timeSource.markNow()
                    currentSlug = slug
                    Result.success(cachedGameScreenshots)
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        } else {
            Result.success(cachedGameScreenshots!!)
        }
    }

    private fun shouldFetch(slug: String): Boolean =
        cachedGameScreenshots == null || currentSlug != slug || (timeStamp + cacheMaxAge).hasPassedNow()
}