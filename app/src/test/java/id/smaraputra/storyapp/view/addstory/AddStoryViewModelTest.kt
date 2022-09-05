package id.smaraputra.storyapp.view.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import id.smaraputra.storyapp.DataDummy
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.remote.response.AddStoryResponse
import id.smaraputra.storyapp.getOrAwaitValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var file: File
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var descriptions: RequestBody
    private lateinit var lat: RequestBody
    private lateinit var lon: RequestBody
    private lateinit var imageMultipart: MultipartBody.Part
    private val dummySuccess = DataDummy.generateSuccessAddNewStory()

    @Before
    fun setUp() {
        descriptions = DataDummy.getRandomUsername().toRequestBody("text/plain".toMediaType())
        lat = 0.0.toString().toRequestBody("text/plain".toMediaType())
        lon = 0.0.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @Test
    fun `when Add Story Success and Should Return Success`() {
        val expectedAddResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedAddResponse.value = Result.Success(dummySuccess)
        `when`(addStoryViewModel.addStory(imageMultipart, descriptions, lat, lon)).thenReturn(expectedAddResponse)
        val actualStory = addStoryViewModel.addStory(imageMultipart, descriptions, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).addStory(imageMultipart, descriptions, lat, lon)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }

    @Test
    fun `when Add Story Failed and Should Return Error`() {
        val expectedAddResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedAddResponse.value = Result.Error("Error")
        `when`(addStoryViewModel.addStory(imageMultipart, descriptions, lat, lon)).thenReturn(expectedAddResponse)
        val actualStory = addStoryViewModel.addStory(imageMultipart, descriptions, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).addStory(imageMultipart, descriptions, lat, lon)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Error)
    }
}