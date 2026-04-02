package com.example.trip_sheet_driver_android.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleSignInHelper {

    fun getClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("714851199284-uss5ujvmuh862k5jml0h4vfgd7maq6tj.apps.googleusercontent.com")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, options)
    }
}