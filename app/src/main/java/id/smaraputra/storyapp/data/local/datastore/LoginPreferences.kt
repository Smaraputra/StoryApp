package id.smaraputra.storyapp.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreferences(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("token_user")
    private val USER_NAME_KEY = stringPreferencesKey("name_user")
    private val STATUS_ONBOARD = booleanPreferencesKey("theme_setting")

    fun getStatusOnBoard(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[STATUS_ONBOARD] ?: false
        }
    }

    suspend fun saveStatusOnBoard(statusOnBoard: Boolean) {
        dataStore.edit { preferences ->
            preferences[STATUS_ONBOARD] = statusOnBoard
        }
    }

    fun getTokenUser(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN_KEY] ?: DEFAULT
        }
    }

    suspend fun saveTokenUser(token :String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    fun getNameUser(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY] ?: DEFAULT
        }
    }

    suspend fun saveNameUser(name :String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null
        const val DEFAULT = "DEFAULT_VALUE"

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}