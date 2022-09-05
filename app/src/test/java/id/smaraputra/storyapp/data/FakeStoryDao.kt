package id.smaraputra.storyapp.data

import androidx.paging.PagingSource
import id.smaraputra.storyapp.PagingSourceUtilsTest
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.data.local.room.StoryDao

class FakeStoryDao : StoryDao {
    private var storyData = mutableListOf<List<StoryModel>>()
    override suspend fun insert(storyModel: List<StoryModel>) {
        storyData.add(storyModel)
    }

    override fun getAllStory(): PagingSource<Int, StoryModel> {
        return PagingSourceUtilsTest(storyData[0])
    }

    override fun getAllStoryDB(): List<StoryModel> {
        lateinit var storyDataLocation : List<StoryModel>
        if(storyData.isNotEmpty()){
            storyDataLocation = storyData[0]
        }
        return storyDataLocation
    }

    override suspend fun deleteAllStory() {
        storyData.clear()
    }
}