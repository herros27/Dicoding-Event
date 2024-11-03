package com.example.submission_1_fundamental_android.di

import android.content.Context
import com.example.submission_1_fundamental_android.data.local.room.EventDatabase
import com.example.submission_1_fundamental_android.data.remote.service.ApiConfig
import com.example.submission_1_fundamental_android.repository.EventRepository
import com.example.submission_1_fundamental_android.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.apiService
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()

        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}