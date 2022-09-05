package id.smaraputra.storyapp.data.local.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PreferencesViewModel(private val pref: LoginPreferences) : ViewModel() {
    fun getStatusOnBoard(): LiveData<Boolean> {
        return pref.getStatusOnBoard().asLiveData()
    }

    fun saveStatusOnBoard(statusOnBoard: Boolean) {
        viewModelScope.launch {
            pref.saveStatusOnBoard(statusOnBoard)
        }
    }

    fun getTokenUser(): LiveData<String> {
        return pref.getTokenUser().asLiveData()
    }

    fun saveTokenUser(token: String) {
        viewModelScope.launch {
            pref.saveTokenUser(token)
        }
    }

    fun getNameUser(): LiveData<String> {
        return pref.getNameUser().asLiveData()
    }

    fun saveNameUser(name: String) {
        viewModelScope.launch {
            pref.saveNameUser(name)
        }
    }
}