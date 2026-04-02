package com.example.trip_sheet_driver_android.pages.dashboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.service.SignatureView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.ncorti.slidetoact.SlideToActView

class TripSignatureActivity : AppCompatActivity() {

    private lateinit var signatureView: SignatureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_signature)

        signatureView = findViewById(R.id.signatureView)

        findViewById<MaterialToolbar>(R.id.toolbarSignature)
            .setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            signatureView.clear()
        }

        val odoInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOdoSignature)

        val slider = findViewById<SlideToActView>(R.id.sliderSubmit)

        // ✅ Slide action
        slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {

                val odo = odoInput.text.toString()

                if (odo.isEmpty()) {
                    Toast.makeText(this@TripSignatureActivity, "Enter odometer reading", Toast.LENGTH_SHORT).show()
                    view.resetSlider()
                    return
                }

                if (signatureView.isEmpty()) {
                    Toast.makeText(
                        this@TripSignatureActivity,
                        "Please provide signature",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.resetSlider()
                    return
                }

                val bitmap: Bitmap = signatureView.getSignatureBitmap()

                // TODO: Upload / Save bitmap
                Toast.makeText(
                    this@TripSignatureActivity,
                    "Signature Captured ✅, Trip Completed!!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        // 🔥 Fade-in animation (OUTSIDE listener)
        slider.alpha = 0f
        slider.animate()
            .alpha(1f)
            .setDuration(800)
            .start()

        // 🔥 Glow effect (OUTSIDE listener)
        val glow = ObjectAnimator.ofFloat(
            slider,
            "alpha",
            1f,
            0.7f,
            1f
        )
        glow.duration = 1500
        glow.repeatCount = ValueAnimator.INFINITE
        glow.start()
    }
}