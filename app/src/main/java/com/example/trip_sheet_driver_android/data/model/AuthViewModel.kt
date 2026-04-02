package com.example.trip_sheet_driver_android.data.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip_sheet_driver_android.api.ApiClient
import com.example.trip_sheet_driver_android.utils.SessionManager
import kotlinx.coroutines.launch
class AuthViewModel : ViewModel() {

    val authState = MutableLiveData<AuthState>()

    fun loginWithGoogle(idToken: String, email: String, name: String) {
        authState.value = AuthState.Loading

        // Simulate backend check
        if (email == "savariraj1@gmail.com") {

            // Existing user
            authState.value = AuthState.ExistingUser(
                User(
                    id = 1,
                    name = "Existing Driver",
                    email = email,
                    role = "DRIVER"
                )
            )

        } else if (email == "raj.s@tylt.co.in") {

            // New user
            authState.value = AuthState.NewUser(
                email = email,
                name = name
            )

        } else {
            authState.value = AuthState.Error("Unauthorized test email")
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class ExistingUser(val user: User) : AuthState()
    data class NewUser(val email: String, val name: String) : AuthState()
    data class Error(val message: String) : AuthState()
}