package com.example.trip_sheet_driver_android.pages.dashboard

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.trip_sheet_driver_android.BaseActivity
import com.example.trip_sheet_driver_android.MainActivity
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.Trip
import com.google.android.material.appbar.MaterialToolbar
import com.ncorti.slidetoact.SlideToActView

class StartTripOtpActivity : BaseActivity() {
    private lateinit var trip: Trip
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_trip_otp)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarOtp)
        toolbar.setNavigationOnClickListener { finish() }
        trip = intent.getParcelableExtra("trip")!!
        val otp1 = findViewById<EditText>(R.id.otp1)
        val otp2 = findViewById<EditText>(R.id.otp2)
        val otp3 = findViewById<EditText>(R.id.otp3)
        val otp4 = findViewById<EditText>(R.id.otp4)
        val odoInput = findViewById<EditText>(R.id.etOdoStart)
        val otpFields = arrayOf(otp1, otp2, otp3, otp4)

        setupOtpInputs(otpFields)

        val slider = findViewById<SlideToActView>(R.id.btnStartTrip)

        // Glow
        slider.alpha = 0f
        slider.animate().alpha(1f).setDuration(800).start()

        val glow = ObjectAnimator.ofFloat(slider, "alpha", 1f, 0.7f, 1f)
        glow.duration = 1500
        glow.repeatCount = ObjectAnimator.INFINITE
        glow.start()

        findViewById<View>(R.id.layoutCall).setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, "tel:8431692290".toUri()))
        }

        findViewById<View>(R.id.layoutSms).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, "sms:8431692290".toUri()))
        }

        findViewById<View>(R.id.layoutChat).setOnClickListener {


            Toast.makeText(this, "Open Chat", Toast.LENGTH_SHORT).show()
        }

        setupFabMenu(findViewById(R.id.fabContainer))
        slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {

                val otp = otpFields.joinToString("") { it.text.toString() }
                val odo = odoInput.text.toString()
                if (odo.isEmpty()) {
                    Toast.makeText(this@StartTripOtpActivity, "Enter odometer reading", Toast.LENGTH_SHORT).show()
                    shakeView(odoInput)
                    view.resetSlider()
                    return
                }

                if (otp.length < 4) {
                    Toast.makeText(this@StartTripOtpActivity, "Enter valid OTP", Toast.LENGTH_SHORT).show()
                    //  SHAKE ALL BOXES
                    otpFields.forEach { shakeView(it) }

                    view.resetSlider()
                } else {
                    Toast.makeText(this@StartTripOtpActivity, "Trip Started!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@StartTripOtpActivity, TripNavigationActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun setupOtpInputs(otpFields: Array<EditText>) {

        for (i in otpFields.indices) {

            val current = otpFields[i]

            // NEXT FOCUS
            current.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpFields.size - 1) {
                        otpFields[i + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // BACKSPACE
            current.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    current.text.isEmpty() &&
                    i > 0
                ) {
                    otpFields[i - 1].requestFocus()
                    otpFields[i - 1].setSelection(otpFields[i - 1].text.length)
                }
                false
            }
        }
    }

    private fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(
            view,
            "translationX",
            0f, 25f, -25f, 20f, -20f, 10f, -10f, 0f
        )
        shake.duration = 400
        shake.start()
    }
}