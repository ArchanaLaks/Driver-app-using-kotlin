package com.example.trip_sheet_driver_android

import android.app.Application
import com.example.trip_sheet_driver_android.utils.SessionManager

class DriverApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}