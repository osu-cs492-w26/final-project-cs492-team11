package edu.oregonstate.cs492.assignmentfinal.data

import com.squareup.moshi.Moshi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RAWGService {
    @GET("games/{id}")
    suspend fun loadGame(
        @Path("id") slug: String,
        @Query("key") key: String
    ) : Response<Game>

    @GET("games/{id}/screenshots")
    suspend fun loadScreenshots(
        @Path("id") slug: String,
        @Query("key") key: String
    ) : Response<GameScreenshots>


    companion object {
        private const val BASE_URL = "https://api.rawg.io/api/"

        fun create() : RAWGService {
            val moshi = Moshi.Builder()
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(RAWGService::class.java)
        }
    }
}