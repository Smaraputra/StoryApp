package id.smaraputra.storyapp.view.register

import androidx.lifecycle.ViewModel
import id.smaraputra.storyapp.data.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun registerUser(map: HashMap<String, String>) = storyRepository.registerUser(map)
}