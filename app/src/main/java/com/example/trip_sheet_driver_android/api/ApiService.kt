package com.example.trip_sheet_driver_android.api

import com.example.trip_sheet_driver_android.data.model.ApiResponse
import com.example.trip_sheet_driver_android.data.model.GoogleLoginRequest
import com.example.trip_sheet_driver_android.data.model.LoginData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/")
    suspend fun pingServer(): Response<ApiResponse<Unit>>

    @POST("auth/google-signup")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<ApiResponse<LoginData>>
}