package id.smaraputra.storyapp.view.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import id.smaraputra.storyapp.DataDummy
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.remote.response.RegisterResponse
import id.smaraputra.storyapp.getOrAwaitValue
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

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var username: String
    private var dummySuccess = DataDummy.generateSuccessRegister()
    private val registerMap: HashMap<String, String> = HashMap()

    @Before
    fun setUp() {
        username = DataDummy.getRandomUsername()
        registerViewModel = RegisterViewModel(storyRepository)
        registerMap["name"] = username
        registerMap["email"] = "$username@$username.com"
        registerMap["password"] = username
    }

    @Test
    fun `when Register Success and Should Return Success`() {
        val expectedRegisterResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedRegisterResponse.value = Result.Success(dummySuccess)
        `when`(registerViewModel.registerUser(registerMap)).thenReturn(expectedRegisterResponse)
        val actualStory = registerViewModel.registerUser(registerMap).getOrAwaitValue()
        Mockito.verify(storyRepository).registerUser(registerMap)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }

    @Test
    fun `when Register Failed and Should Return Error`() {
        val expectedRegisterResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedRegisterResponse.value = Result.Error("Error")
        `when`(registerViewModel.registerUser(registerMap)).thenReturn(expectedRegisterResponse)
        val actualStory = registerViewModel.registerUser(registerMap).getOrAwaitValue()
        Mockito.verify(storyRepository).registerUser(registerMap)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Error)
    }
}