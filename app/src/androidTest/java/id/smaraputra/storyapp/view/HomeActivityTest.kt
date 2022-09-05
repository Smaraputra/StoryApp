package id.smaraputra.storyapp.view

import android.Manifest
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.utils.EspressoIdlingResource
import id.smaraputra.storyapp.view.addstory.AddStoryActivity
import id.smaraputra.storyapp.view.detailstory.DetailStoryActivity
import id.smaraputra.storyapp.view.mycamera.CameraActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.GrantPermissionRule

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeActivityTest{
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    val activity = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadStory() {
        onView(withId(R.id.rvUser)).check(matches(isDisplayed()))
        onView(withId(R.id.rvUser)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
    }

    @Test
    fun loadStoryMap() {
        onView(withId(R.id.rvUser)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_map)).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun settingLogout() {
        Intents.init()
        onView(withId(R.id.rvUser)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_setting)).perform(click())
        onView(withId(R.id.logoutButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()))
        onView(withId(R.id.cancelButton)).perform(click())
        Intents.release()
    }

    @Test
    fun createNewStory() {
        Intents.init()
        onView(withId(R.id.fabAddStory)).check(matches(isDisplayed()))
        onView(withId(R.id.fabAddStory)).perform(click())
        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.descriptionNew)).check(matches(isDisplayed()))
        onView(withId(R.id.descriptionNew)).perform(typeText("Ini deskripsi."), closeSoftKeyboard())
        onView(withId(R.id.cameraXButton)).perform(click())
        intended(hasComponent(CameraActivity::class.java.name))
        onView(withId(R.id.captureImage)).perform(click())
        Intents.release()
    }

    @Test
    fun loadDetailStory() {
        Intents.init()
        onView(withId(R.id.rvUser)).check(matches(isDisplayed()))
        onView(withId(R.id.rvUser)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        intended(hasComponent(DetailStoryActivity::class.java.name))
        onView(withId(R.id.imageView4)).check(matches(isDisplayed()))
        Intents.release()
    }
}