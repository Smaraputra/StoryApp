package id.smaraputra.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import id.smaraputra.storyapp.data.local.entity.StoryLocationModel
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.data.local.room.StoryRoomDatabase
import id.smaraputra.storyapp.data.remote.response.AddStoryResponse
import id.smaraputra.storyapp.data.remote.response.AllStoryResponse
import id.smaraputra.storyapp.data.remote.response.LoginResponse
import id.smaraputra.storyapp.data.remote.response.RegisterResponse
import id.smaraputra.storyapp.data.remote.retrofit.ServicesAPI
import id.smaraputra.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(
    private val storyRoomDatabase: StoryRoomDatabase,
    private val apiService: ServicesAPI
    ) {
    private val result = MediatorLiveData<Result<List<StoryLocationModel>>>()
    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()
    private val resultRegister = MediatorLiveData<Result<RegisterResponse>>()
    private val resultAddStory = MediatorLiveData<Result<AddStoryResponse>>()

    fun getStory(): LiveData<PagingData<StoryModel>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(storyRoomDatabase, apiService),
                pagingSourceFactory = {
                    storyRoomDatabase.storyDao().getAllStory()
                }
            ).liveData
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStoryLocation(): LiveData<Result<List<StoryLocationModel>>> {
        result.value = Result.Loading
        wrapEspressoIdlingResource {
            val client = apiService.listStoryLocation()
            client.enqueue(object : Callback<AllStoryResponse> {
                override fun onResponse(call: Call<AllStoryResponse>, response: Response<AllStoryResponse>) {
                    if (response.isSuccessful) {
                        val story = response.body()?.listStory
                        val storyList = ArrayList<StoryLocationModel>()
                        GlobalScope.launch(Dispatchers.Main) {
                            story?.forEach { story ->
                                val news = StoryLocationModel(
                                    story.photoUrl,
                                    story.createdAt,
                                    story.name,
                                    story.description,
                                    story.lon,
                                    story.id,
                                    story.lat,
                                )
                                storyList.add(news)
                            }
                            storyRoomDatabase.storyLocationDao().deleteAllStory()
                            storyRoomDatabase.storyLocationDao().insert(storyList)
                        }
                    }
                }

                override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                    result.value = Result.Error(t.message.toString())
                }
            })
            GlobalScope.launch(Dispatchers.Main) {
                val localData = storyRoomDatabase.storyLocationDao().getAllStory()
                result.addSource(localData) { newData: List<StoryLocationModel> ->
                    result.value = Result.Success(newData)
                }
            }
            return result
        }
    }

    fun addStory(imageMultipart: MultipartBody.Part, description: RequestBody, lat: RequestBody,
                 lon: RequestBody): LiveData<Result<AddStoryResponse>> {
        resultAddStory.value = Result.Loading
        val client = apiService.uploadStory(imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                if(response.isSuccessful){
                    resultAddStory.value = Result.Success(response.body() as AddStoryResponse)
                }else{
                    lateinit var jsonObject: JSONObject
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        resultAddStory.value = Result.Error(jsonObject.getString("message"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                resultAddStory.value = Result.Error(t.message.toString())
            }
        })
        return resultAddStory
    }

    fun registerUser(registerMap: HashMap<String, String>): LiveData<Result<RegisterResponse>> {
        resultRegister.value = Result.Loading
        val client = apiService.registerUser(registerMap)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if(response.isSuccessful){
                    resultRegister.value = Result.Success(response.body() as RegisterResponse)
                }else{
                    lateinit var jsonObject: JSONObject
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        resultRegister.value = Result.Error(jsonObject.getString("message"))
                    }catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                resultRegister.value = Result.Error(t.message.toString())
            }
        })
        return resultRegister
    }

    fun loginUser(loginMap: HashMap<String, String>) : LiveData<Result<LoginResponse>> {
        resultLogin.value = Result.Loading
        val client = apiService.loginUser(loginMap)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    resultLogin.value = Result.Success(response.body() as LoginResponse)
                }else{
                    lateinit var jsonObject: JSONObject
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        resultLogin.value = Result.Error(jsonObject.getString("message"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                resultLogin.value = Result.Error(t.message.toString())
            }
        })
        return resultLogin
    }
}