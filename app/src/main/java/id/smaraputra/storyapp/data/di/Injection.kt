package id.smaraputra.storyapp.data.di

import android.content.Context
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.local.room.StoryRoomDatabase
import id.smaraputra.storyapp.data.remote.retrofit.ConfigAPI

object Injection {
    fun provideRepository(context: Context, token: String): StoryRepository {
        val database = StoryRoomDatabase.getDatabase(context)
        val apiService = ConfigAPI.getApiService(token)
        return StoryRepository(database, apiService)
    }
}