package id.smaraputra.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.data.local.room.StoryLocationDao


class FakeStoryLocationDao : StoryLocationDao {
    private var storyData = mutableListOf<List<StoryLocationModel>>()

    override suspend fun insert(storyModel: List<StoryLocationModel>) {
        storyData.add(storyModel)
    }

    override fun getAllStory(): LiveData<List<StoryLocationModel>> {
        val storyDataLocation = MutableLiveData<List<StoryLocationModel>>()
        if(storyData.isNotEmpty()){
            storyDataLocation.value = storyData[0]
        }
        return storyDataLocation
    }

    override suspend fun deleteAllStory() {
        storyData.clear()
    }

}