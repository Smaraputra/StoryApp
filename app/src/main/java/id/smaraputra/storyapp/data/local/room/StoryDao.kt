package id.smaraputra.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import id.smaraputra.storyapp.data.local.entity.StoryModel

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(storyModel: List<StoryModel>)

    @Query("SELECT * from story_user ORDER BY createdAt DESC")
    fun getAllStory(): PagingSource<Int, StoryModel>

    @Query("SELECT * from story_user ORDER BY createdAt DESC")
    fun getAllStoryDB(): List<StoryModel>

    @Query("DELETE FROM story_user")
    suspend fun deleteAllStory()
}