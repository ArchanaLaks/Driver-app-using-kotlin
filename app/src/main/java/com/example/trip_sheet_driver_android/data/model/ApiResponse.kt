package com.example.trip_sheet_driver_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class GoogleLoginRequest(
    val idToken: String
)

data class LoginData(
    val token: String,
    val isNewUser: Boolean,
    val email: String?,
    val name: String?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

@Parcelize
data class Trip(
    val id: Int,
    val passenger: String,
    val organisation: String,
    val pickup: String,
    val drop: String,
    val startTime: Long,
    var status: String
) : Parcelable