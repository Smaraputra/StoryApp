package id.smaraputra.storyapp.view.liststory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import id.smaraputra.storyapp.data.StoryRepository

class ListViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun listStory() = storyRepository.getStory().cachedIn(viewModelScope)
    fun listStoryLocation() = storyRepository.getStoryLocation()
}