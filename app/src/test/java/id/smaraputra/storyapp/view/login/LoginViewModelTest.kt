package id.smaraputra.storyapp.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import id.smaraputra.storyapp.DataDummy
import id.smaraputra.storyapp.data.Result
import id.smaraputra.storyapp.data.StoryRepository
import id.smaraputra.storyapp.data.remote.response.LoginResponse
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var username: String
    private var dummySuccess = DataDummy.generateSuccessLogin()
    private val loginMap: HashMap<String, String> = HashMap()

    @Before
    fun setUp() {
        username = DataDummy.getRandomUsername()
        loginViewModel = LoginViewModel(storyRepository)
        loginMap["email"] = "$username@$username.com"
        loginMap["password"] = username
    }

    @Test
    fun `when Login Success and Should Return Success`() {
        val expectedRegisterResponse = MutableLiveData<Result<LoginResponse>>()
        expectedRegisterResponse.value = Result.Success(dummySuccess)
        `when`(loginViewModel.loginUser(loginMap)).thenReturn(expectedRegisterResponse)
        val actualStory = loginViewModel.loginUser(loginMap).getOrAwaitValue()
        Mockito.verify(storyRepository).loginUser(loginMap)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }

    @Test
    fun `when Login Failed and Should Return Error`() {
        val expectedRegisterResponse = MutableLiveData<Result<LoginResponse>>()
        expectedRegisterResponse.value = Result.Error("Error")
        `when`(loginViewModel.loginUser(loginMap)).thenReturn(expectedRegisterResponse)
        val actualStory = loginViewModel.loginUser(loginMap).getOrAwaitValue()
        Mockito.verify(storyRepository).loginUser(loginMap)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Error)
    }
}