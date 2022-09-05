package id.smaraputra.storyapp.view.addstory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.di.Injection

class AddStoryViewModelFactory private constructor(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
 
    companion object {
        @Volatile
        private var instance: AddStoryViewModelFactory? = null
        fun getInstance(context: Context, token: String): AddStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddStoryViewModelFactory(Injection.provideRepository(context, token))
            }.also { instance = it }
    }
}