package com.example.trip_sheet_driver_android.utils
import android.content.Context

object SessionManager {

    private const val PREF = "driver_pref"
    private const val TOKEN = "token"

    private lateinit var prefs: android.content.SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}