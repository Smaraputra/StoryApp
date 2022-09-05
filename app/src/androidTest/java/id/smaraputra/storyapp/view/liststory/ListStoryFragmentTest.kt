package id.smaraputra.storyapp.view.liststory

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import id.smaraputra.storyapp.JsonConverter
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.remote.retrofit.ConfigAPI
import id.smaraputra.storyapp.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ListStoryFragmentTest {
    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ConfigAPI.urlBase = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStory_Result_Success() {
        launchFragmentInContainer<ListStoryFragment>(null, R.style.Theme_StoryApp)
        val mockResponse = MockResponse().setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.rvUser)).check(matches(isDisplayed()))
        onView(withText("Akagami")).check(matches(isDisplayed()))
        onView(withId(R.id.rvUser))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("tes apakah ke no 1 gk"))
                )
            )
    }

    @Test
    fun getStory_Result_Error() {
        launchFragmentInContainer<ListStoryFragment>(null, R.style.Theme_StoryApp)
        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.noData)).check(matches(isDisplayed()))
    }
}