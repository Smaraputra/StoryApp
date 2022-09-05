package id.smaraputra.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import id.smaraputra.storyapp.DataDummy
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.data.remote.response.LoginResponse
import id.smaraputra.storyapp.data.remote.response.RegisterResponse
import id.smaraputra.storyapp.getOrAwaitValue
import id.smaraputra.storyapp.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyDao: FakeStoryDao
    private lateinit var storyLocationDao: FakeStoryLocationDao

    @Before
    fun setUp() {
        storyDao = FakeStoryDao()
        storyLocationDao = FakeStoryLocationDao()
    }

    @Test
    fun `when get Story Data Widget Should Exist in Room`() = mainCoroutineRule.runBlockingTest {
        val sampleStory = DataDummy.generateDummyStoryEntity()
        storyDao.insert(sampleStory)
        val actualStory = storyDao.getAllStoryDB()
        assertNotNull(actualStory)
        assertEquals(sampleStory.size, actualStory.size)
    }

    @Test
    fun `when get Story Recalled Data Widget Should Cleared in Room`() = mainCoroutineRule.runBlockingTest {
        val sampleStory = DataDummy.generateDummyStoryEntity()
        storyDao.insert(sampleStory)
        val currentStory = storyDao.getAllStoryDB()
        storyDao.deleteAllStory()
        assert(currentStory.isNotEmpty())
        assertThrows(UninitializedPropertyAccessException::class.java){
            storyDao.getAllStoryDB()
        }
    }

    @Test
    fun `when get Story Data Paging Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val sampleStory = DataDummy.generateDummyStoryEntity()
        storyDao.insert(sampleStory)
        val pagingSourceFactory = { storyDao.getAllStory() }
        val pagingDataFlow: LiveData<PagingData<StoryModel>> = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
        Mockito.`when`(storyRepository.getStory()).thenReturn(pagingDataFlow)
        val actualStory = storyRepository.getStory().getOrAwaitValue()
        assertNotNull(actualStory)
    }

    @Test
    fun `when get Story Location Data Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedStory = MutableLiveData<Result<List<StoryLocationModel>>>()
        expectedStory.value = Result.Success(DataDummy.generateDummyStoryLocationEntity())
        Mockito.`when`(storyRepository.getStoryLocation()).thenReturn(expectedStory)
        val actualStory = storyRepository.getStoryLocation().getOrAwaitValue()
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }

    @Test
    fun `when get Story Location Data Should Exist in Room`() = mainCoroutineRule.runBlockingTest {
        val sampleStory = DataDummy.generateDummyStoryLocationEntity()
        storyLocationDao.insert(sampleStory)
        val actualStory = storyLocationDao.getAllStory().getOrAwaitValue()
        assertNotNull(actualStory)
        assertEquals(sampleStory.size, actualStory.size)
    }

    @Test
    fun `when get Story Location Recalled Data Should Cleared in Room`() = mainCoroutineRule.runBlockingTest {
        val sampleStory = DataDummy.generateDummyStoryLocationEntity()
        storyLocationDao.insert(sampleStory)
        val currentStory = storyLocationDao.getAllStory().getOrAwaitValue()
        storyLocationDao.deleteAllStory()
        assert(currentStory.isNotEmpty())
        assertThrows(TimeoutException::class.java){
            storyLocationDao.getAllStory().getOrAwaitValue()
        }
    }

    @Test
    fun `when Login Success Should Return Success`() = mainCoroutineRule.runBlockingTest {
        val dummy = DataDummy.generateLoginMap()
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(DataDummy.generateSuccessLogin())
        Mockito.`when`(storyRepository.loginUser(dummy)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.loginUser(dummy).getOrAwaitValue()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Login Failed Should Return Error`() = mainCoroutineRule.runBlockingTest {
        val dummy = DataDummy.generateLoginMap()
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")
        Mockito.`when`(storyRepository.loginUser(dummy)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.loginUser(dummy).getOrAwaitValue()
        assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when Register Success Should Return Success`() = mainCoroutineRule.runBlockingTest {
        val dummy = DataDummy.generateRegisterMap()
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Success(DataDummy.generateSuccessRegister())
        Mockito.`when`(storyRepository.registerUser(dummy)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.registerUser(dummy).getOrAwaitValue()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Register Failed Should Return Error`() = mainCoroutineRule.runBlockingTest {
        val dummy = DataDummy.generateRegisterMap()
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Error("Error")
        Mockito.`when`(storyRepository.registerUser(dummy)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.registerUser(dummy).getOrAwaitValue()
        assertTrue(actualResponse is Result.Error)
    }
}