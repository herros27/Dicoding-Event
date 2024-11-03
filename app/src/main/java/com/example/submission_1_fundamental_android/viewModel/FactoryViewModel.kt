package com.example.submission_1_fundamental_android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submission_1_fundamental_android.data.preference.SettingsPreference
import com.example.submission_1_fundamental_android.data.preference.dataStore
import com.example.submission_1_fundamental_android.di.Injection
import com.example.submission_1_fundamental_android.repository.EventRepository

class FactoryViewModel (
    private val preference: SettingsPreference,
    private val eventRepository: EventRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) ->{
                MainViewModel(preference,eventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class : ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: FactoryViewModel? = null
        fun getInstance(context: Context): FactoryViewModel =
            instance ?: synchronized(this) {
                instance ?: FactoryViewModel(
                    SettingsPreference.getInstance(context.dataStore),
                    Injection.provideRepository(context)
                )
            }.also { instance = it }
    }
}