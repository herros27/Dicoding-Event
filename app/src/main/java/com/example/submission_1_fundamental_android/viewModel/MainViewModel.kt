package com.example.submission_1_fundamental_android.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.data.preference.SettingsPreference
import com.example.submission_1_fundamental_android.data.remote.response.ListEventsItem
import com.example.submission_1_fundamental_android.repository.EventRepository
import kotlinx.coroutines.launch
import com.example.submission_1_fundamental_android.utils.Result

class MainViewModel(
    private val preference: SettingsPreference,
    private val repository: EventRepository
):ViewModel() {
    private val _events = MutableLiveData<Result<EventEntity>>()
    val events: LiveData<Result<EventEntity>> = _events

    private val _searchResult = MutableLiveData<Result<List<ListEventsItem>>>()
    val searchResult: LiveData<Result<List<ListEventsItem>>> = _searchResult

    fun getThemeSetting(): LiveData<Boolean>{
        return preference.getTheme().asLiveData()
    }

    fun setThemeSetting(isDarkMode: Boolean){
        viewModelScope.launch {
            preference.setThemeSetting(isDarkMode)
        }
    }

    fun getNotifSetting(): LiveData<Boolean>{
        return preference.getNotifSetting().asLiveData()
    }

    fun setNotifSetting(isActive: Boolean){
        viewModelScope.launch {
            preference.setNotiffSetting(isActive)
        }
    }

    fun searchEvents(query: String){
        _searchResult.value = Result.Loading

        viewModelScope.launch {
            try {

                val result = repository.searchEvents(query)
                _searchResult.value = when(result){
                    is Result.Success -> Result.Success(result.data)
                    else -> result
                }
            } catch (e: Exception) {
                _searchResult.value = Result.Error(e.toString())
            }
        }
    }

    fun getEventByIdSync(eventId: Int) = viewModelScope.launch {
        try {
            _events.value = Result.Loading
            val eventData = repository.getEventByIdSync(eventId)

            if (eventData != null) {
                _events.value = Result.Success(eventData)
            } else {
                _events.value = Result.Error("Event not found")
            }
        }catch (_: Exception){

        }
//        viewModelScope.launch {
//
//        }
    }


    fun getUpcomingEvent(): LiveData<Result<List<EventEntity>>>{
        return repository.getAllEvents(1)
    }

    fun getFinishedEvent(): LiveData<Result<List<EventEntity>>>{
        return repository.getAllEvents(0)
    }

    fun getFavorite():LiveData<List<EventEntity>>{
        return repository.getFavorite()
    }

    fun addFavoriteEvent(event: EventEntity){
        viewModelScope.launch {
            repository.setFavorite(event, true)
        }
    }

    fun removeFavoriteEvent(event: EventEntity){
        viewModelScope.launch {
            repository.setFavorite(event, false)
        }
    }

}