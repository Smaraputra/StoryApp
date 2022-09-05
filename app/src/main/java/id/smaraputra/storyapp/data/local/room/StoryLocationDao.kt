package id.smaraputra.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel

@Dao
interface StoryLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(storyModel: List<StoryLocationModel>)

    @Query("SELECT * from story_location_user ORDER BY createdAt DESC")
    fun getAllStory(): LiveData<List<StoryLocationModel>>

    @Query("DELETE FROM story_location_user")
    suspend fun deleteAllStory()
}