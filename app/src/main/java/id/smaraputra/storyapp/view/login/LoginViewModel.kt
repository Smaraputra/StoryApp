package id.smaraputra.storyapp.view.login

import androidx.lifecycle.ViewModel
import id.smaraputra.storyapp.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun loginUser(loginMap: HashMap<String, String>) = storyRepository.loginUser(loginMap)
}