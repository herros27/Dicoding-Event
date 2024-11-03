package com.example.submission_1_fundamental_android.data.remote.service
import com.example.submission_1_fundamental_android.data.remote.response.EventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse 
    @GET("events")
    suspend fun getUpdatedEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Response<EventResponse>

}