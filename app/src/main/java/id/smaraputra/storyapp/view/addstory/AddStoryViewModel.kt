package id.smaraputra.storyapp.view.addstory

import androidx.lifecycle.ViewModel
import id.smaraputra.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun addStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody, lat:
        RequestBody, lon: RequestBody
    ) = storyRepository.addStory(imageMultipart, description, lat, lon)
}