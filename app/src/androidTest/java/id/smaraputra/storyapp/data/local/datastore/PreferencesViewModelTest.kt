package id.smaraputra.storyapp.data.local.datastore

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import id.smaraputra.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PreferencesViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var loginPreferences: LoginPreferences
    private var token: String = "iniTokenTest"
    private var username: String = "iniUsernameTest"
    private var status: Boolean = true
    private val testContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher + Job())
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile =
            { testContext.preferencesDataStoreFile("test-preferences-file") }
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
        loginPreferences = LoginPreferences(testDataStore)
        preferencesViewModel = PreferencesViewModel(loginPreferences)
    }

    @Test
    fun whenSavedAndGetTokenIsSame() {
        preferencesViewModel.saveTokenUser(token)
        val getToken = preferencesViewModel.getTokenUser().getOrAwaitValue()
        assertEquals(getToken, token)
    }

    @Test
    fun whenSavedAndGetNameIsSame() {
        preferencesViewModel.saveNameUser(username)
        val getUsername = preferencesViewModel.getNameUser().getOrAwaitValue()
        assertEquals(getUsername, username)
    }

    @Test
    fun whenSavedAndGetStatusIsSame() {
        preferencesViewModel.saveStatusOnBoard(status)
        val getStatus = preferencesViewModel.getStatusOnBoard().getOrAwaitValue()
        assertEquals(getStatus, true)
    }

    @After
    fun cleanUp() {
        testCoroutineScope.runBlockingTest {
            testDataStore.edit { it.clear() }
        }
        testCoroutineScope.cancel()
    }
}