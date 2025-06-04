package com.example.bytedancecamplab2

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.bytedancecamplab2.NoteDataBaseHelper.InfoCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewViewModel(application: Application) : AndroidViewModel(application) {
    private val _infoCardList = MutableLiveData<List<InfoCard>>()
    val infoCardList: LiveData<List<InfoCard>> get() = _infoCardList

    init {
        loadInfoCardForCurrentUser()
    }

    fun refreshData(userId: Long) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                NoteDataBaseHelper(application.applicationContext)
                    .findInfoCardByUserId(userId)
            }
            _infoCardList.postValue(data)
        }
    }

    private fun loadInfoCardForCurrentUser() {
        val userId = getApplication<Application>().getSharedPreferences(
            "userStatus",
            MODE_PRIVATE
        ).getLong("userId", -1L)
        refreshData(userId)
    }
}