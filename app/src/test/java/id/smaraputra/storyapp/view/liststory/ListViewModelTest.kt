package id.smaraputra.storyapp.view.liststory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import id.smaraputra.storyapp.DataDummy
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var listViewModel: ListViewModel
    private val dummyStory = DataDummy.generateDummyStoryEntity()
    private val dummyStoryLocation = DataDummy.generateDummyStoryLocationEntity()

    @Before
    fun setUp() {
        listViewModel = ListViewModel(storyRepository)
    }

    @Test
    fun `when Get Map Story Should Not Null and Return Success`() {
        val expectedStory = MutableLiveData<Result<List<StoryLocationModel>>>()
        expectedStory.value = Result.Success(dummyStoryLocation)
        `when`(listViewModel.listStoryLocation()).thenReturn(expectedStory)
        val actualStory = listViewModel.listStoryLocation().getOrAwaitValue()
        Mockito.verify(storyRepository).getStoryLocation()
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Success)
        Assert.assertEquals(dummyStory.size, (actualStory as Result.Success).data.size)
    }

    @Test
    fun `when Map Network Error Should Return Error`() {
        val expectedStory = MutableLiveData<Result<List<StoryLocationModel>>>()
        expectedStory.value = Result.Error("Error")
        `when`(listViewModel.listStoryLocation()).thenReturn(expectedStory)
        val actualStory = listViewModel.listStoryLocation().getOrAwaitValue()
        Mockito.verify(storyRepository).getStoryLocation()
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Error)
    }
}