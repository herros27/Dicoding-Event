package com.example.submission_1_fundamental_android.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.submission_1_fundamental_android.data.local.EventEntity

@Dao
interface EventDao {


    @Query("SELECT * FROM event where isFavourite = 1")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Query("""
        SELECT * FROM event 
        WHERE (isFinished = 1 OR isUpcoming = 1)
        AND(name LIKE '%' || :query || '%')
    """)
    fun searchAllEvent(query : String) : LiveData<List<EventEntity>>

    @Query("DELETE FROM event WHERE isFavourite = 0")
    fun deleteAll()

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: String): LiveData<EventEntity>

    @Query("SELECT * FROM event WHERE id = :eventId")
    suspend fun getEventByIdSync(eventId: Int): EventEntity?

    @Query("SELECT EXISTS(SELECT * FROM event WHERE isFavourite = 1 AND name = :name)")
    suspend fun isEventFavourite(name: String) : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(news: List<EventEntity>)

    @Update
    suspend fun updateFavoriteData(events: EventEntity)
}