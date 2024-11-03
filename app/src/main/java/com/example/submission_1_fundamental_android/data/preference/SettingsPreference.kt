package com.example.submission_1_fundamental_android.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SettingsPreference private constructor(private val dataStore: DataStore<Preferences>){

    private val keyTheme = booleanPreferencesKey(THEME)
    private val keyNotification = booleanPreferencesKey(NOTIFICATION)

    fun getTheme() : Flow<Boolean>   {
        return dataStore.data.map{
            it[keyTheme] ?: false
        }
    }

    fun getNotifSetting() : Flow<Boolean> {
        return dataStore.data.map {
            it[keyNotification] ?: false
        }
    }

    suspend fun setNotiffSetting(isActive : Boolean){
        dataStore.edit {
            it[keyNotification] = isActive
        }
    }

    suspend fun setThemeSetting(isDarkMode : Boolean){
        dataStore.edit {
            it[keyTheme] = isDarkMode
        }
    }

    companion object{
        @Volatile
        private var instance: SettingsPreference?= null
        const val THEME = "setting_theme"
        const val NOTIFICATION = "setting_notification"

        fun getInstance(dataStore: DataStore<Preferences>) : SettingsPreference =
            instance ?: synchronized(this){
                instance ?: SettingsPreference(dataStore)
            }.also { instance = it }
    }
}