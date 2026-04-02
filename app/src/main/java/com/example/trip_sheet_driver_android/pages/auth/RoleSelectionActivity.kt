package com.example.trip_sheet_driver_android.pages.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.trip_sheet_driver_android.R
import com.google.android.material.button.MaterialButton

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true

        findViewById<MaterialButton>(R.id.btnDriver).setOnClickListener {
            startActivity(Intent(this, DriverOwnerLinkActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnOwnerDriver).setOnClickListener {
//            startActivity(Intent(this, OwnerSetupActivity::class.java))
        }
    }
}