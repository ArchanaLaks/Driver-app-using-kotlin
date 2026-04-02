package com.example.trip_sheet_driver_android.pages.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.trip_sheet_driver_android.MainActivity
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.AuthState
import com.example.trip_sheet_driver_android.data.model.AuthViewModel
import com.example.trip_sheet_driver_android.pages.dashboard.DashboardActivity
import com.example.trip_sheet_driver_android.utils.GoogleSignInHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var googleClient: GoogleSignInClient
    private lateinit var viewModel: AuthViewModel
    private lateinit var btnGoogleLogin: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.result
//                viewModel.loginWithGoogle(account.idToken!!)
                val email = account.email ?: ""
                val name = account.displayName ?: ""

                viewModel.loginWithGoogle(account.idToken!!, email, name)
            } else {
                showLoading(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Top up bar icon color
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true

        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        progressBar = findViewById(R.id.progressBar)

        googleClient = GoogleSignInHelper.getClient(this)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        btnGoogleLogin.setOnClickListener {
            showLoading(true)
            googleSignInLauncher.launch(googleClient.signInIntent)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->

            when (state) {

                is AuthState.Loading -> {
                    showLoading(true)
                }

                is AuthState.ExistingUser -> {
                    showLoading(false)
                    goDashboard()
                }

                is AuthState.NewUser -> {
                    showLoading(false)
                    val intent = Intent(this, RoleSelectionActivity::class.java)
                    intent.putExtra("email", state.email)
                    intent.putExtra("name", state.name)
                    startActivity(intent)
                    finish()
                }

                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        btnGoogleLogin.isEnabled = !isLoading
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}