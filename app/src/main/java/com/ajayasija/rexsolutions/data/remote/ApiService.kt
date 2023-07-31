package com.ajayasija.rexsolutions.data.remote

import com.ajayasija.rexsolutions.domain.model.LoginModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("login_handler.php")
    suspend fun login(
        @Query("user_name") userName: String,
        @Query("user_pass") userPass: String
    ): LoginModel

    @GET("login_handler.php")
    fun login1(
        @Query("user_name") userName: String,
        @Query("user_pass") userPass: String
    ): Call<LoginModel>

}