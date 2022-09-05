package id.smaraputra.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.smaraputra.storyapp.data.local.entity.RemoteKeysModel
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.data.local.entity.StoryModel

@Database(entities = [StoryModel::class, StoryLocationModel::class, RemoteKeysModel::class], version = 3)
abstract class StoryRoomDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun storyLocationDao(): StoryLocationDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    companion object {
        @Volatile
        private var INSTANCE: StoryRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): StoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryRoomDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}