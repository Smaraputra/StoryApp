package id.smaraputra.storyapp

import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.data.remote.response.AddStoryResponse
import id.smaraputra.storyapp.data.remote.response.LoginResponse
import id.smaraputra.storyapp.data.remote.response.LoginResult
import id.smaraputra.storyapp.data.remote.response.RegisterResponse
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryModel> {
        val storyList = ArrayList<StoryModel>()
        for (i in 0..10) {
            val story = StoryModel(
                "https://www.google.com/",
                "2022-02-22T22:22:22Z",
                "testing",
                "testingDescription",
                0.0,
                "$i",
                0.0
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyStoryLocationEntity(): List<StoryLocationModel> {
        val storyList = ArrayList<StoryLocationModel>()
        for (i in 0..10) {
            val story = StoryLocationModel(
                "https://www.google.com/",
                "2022-02-22T22:22:22Z",
                "testing",
                "testingDescription",
                30.00,
                "$i",
                30.00
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateSuccessRegister(): RegisterResponse {
        return RegisterResponse(
            false,
            "User Created",
        )
    }

    fun generateSuccessAddNewStory(): AddStoryResponse {
        return AddStoryResponse(
            false,
            "success",
        )
    }

    fun generateSuccessLogin(): LoginResponse {
        return LoginResponse(
            LoginResult(getRandomUsername(), "user-as14s1414asf", "Token"),
            false, "Success")
    }

    fun getRandomUsername(): String {
        return Calendar.getInstance().time.time.toString()
    }

    fun generateLoginMap(): HashMap<String, String>{
        val username: String = getRandomUsername()
        val loginMap: HashMap<String, String> = HashMap()
        loginMap["email"] = "$username@$username.com"
        loginMap["password"] = username
        return loginMap
    }

    fun generateRegisterMap(): HashMap<String, String>{
        val username: String = getRandomUsername()
        val registerMap: HashMap<String, String> = HashMap()
        registerMap["name"] = username
        registerMap["email"] = "$username@$username.com"
        registerMap["password"] = username
        return registerMap
    }
}