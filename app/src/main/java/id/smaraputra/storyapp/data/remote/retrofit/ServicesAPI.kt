package id.smaraputra.storyapp.data.remote.retrofit

import id.smaraputra.storyapp.data.remote.response.AddStoryResponse
import id.smaraputra.storyapp.data.remote.response.AllStoryResponse
import id.smaraputra.storyapp.data.remote.response.LoginResponse
import id.smaraputra.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServicesAPI {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @FieldMap hashMap: Map<String, String>
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @FieldMap hashMap: Map<String, String>
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun listStory(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<AllStoryResponse>

    @GET("stories?location=1")
    fun listStoryLocation(): Call<AllStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): Call<AddStoryResponse>
}