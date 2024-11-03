package com.example.submission_1_fundamental_android.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.data.local.room.EventDao
import com.example.submission_1_fundamental_android.data.remote.response.EventResponse
import com.example.submission_1_fundamental_android.data.remote.response.ListEventsItem
import com.example.submission_1_fundamental_android.data.remote.service.ApiService
import com.example.submission_1_fundamental_android.utils.AppExecutors
import com.example.submission_1_fundamental_android.utils.Result
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

class EventRepository private constructor(
    private val api: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {

    fun getAllEvents(active: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = api.getEvents(active)

            val data = response.listEvents
            val eventList = data.map { event ->
                val isFinished = active == 0
                val isUpcoming = active == 1
                val isFavourite = event.name.let {
                    eventDao.isEventFavourite(it)
                }
                EventEntity(
                    event.id,
                    event.name,
                    event.summary,
                    event.description,
                    event.imageLogo,
                    event.mediaCover,
                    event.category,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.registrants,
                    event.beginTime,
                    event.endTime,
                    event.link,
                    isUpcoming,
                    isFavourite,
                    isFinished
                )
            }

            eventDao.insertEvents(eventList)
            emit(Result.Success(eventList))

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getEventByIdSync(eventId: Int): EventEntity? {
        return eventDao.getEventByIdSync(eventId)
    }

    suspend fun searchEvents(query: String): Result<List<ListEventsItem>> {
        return try {
            val response = api.searchEvents(query = query)
            if (response.isSuccessful) {
                val eventResponse = response.body()
                if (eventResponse != null && !eventResponse.error) {
                    Result.Success(eventResponse.listEvents)
                } else {
                    Result.Error(Exception("Failed fetching data...").toString())
                }
            } else {
                Result.Error(Exception("Network request failed").toString())
            }
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }



    suspend fun setFavorite(event:EventEntity, favorite: Boolean){
        event.isFavorite = favorite
        withContext(appExecutors.diskIO.asCoroutineDispatcher()){
            eventDao.updateFavoriteData(event)
        }
    }

    fun getFavorite(): LiveData<List<EventEntity>>{
        return eventDao.getFavoriteEvent()
    }

    suspend fun getNearestEvent() : EventResponse? {
        val getEvent =  try {
            api.getUpdatedEvent(active = -1, limit = 1)
        } catch (e: Exception) {
            null
        }
        return getEvent
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }

    fun searchEvent(query: String):LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val dataLocal = eventDao.searchAllEvent(query).map { eventList ->
                if (eventList.isNotEmpty()) {
                    Result.Success(eventList)
                } else {
                    Result.Error("No events found for the query: $query")
                }
            }
            emitSource(dataLocal)
        } catch (exception: Exception) {
            emit(Result.Error(exception.message.toString()))
        }
    }
}
