package id.smaraputra.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import id.smaraputra.storyapp.data.local.entity.StoryModel

data class AllStoryResponse(
	@field:SerializedName("listStory")
	val listStory: List<StoryModel>?,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
