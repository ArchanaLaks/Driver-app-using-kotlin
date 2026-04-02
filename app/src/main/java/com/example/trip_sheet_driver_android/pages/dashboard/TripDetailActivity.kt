package com.example.trip_sheet_driver_android.pages.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.trip_sheet_driver_android.BaseActivity
import com.example.trip_sheet_driver_android.BuildConfig
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.Trip
import com.example.trip_sheet_driver_android.pages.trip.DriverChatActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.PolyUtil
import com.ncorti.slidetoact.SlideToActView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class TripDetailsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var trip: Trip
    private val client = OkHttpClient()

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_details)

        trip = intent.getParcelableExtra("trip")!!

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<MaterialButton>(R.id.btnNavigate)
            .setOnClickListener { openGoogleMapsNavigation() }

        findViewById<View>(R.id.fabCall).setOnClickListener {
            openDialer("8431692290")
        }

        findViewById<View>(R.id.fabSms).setOnClickListener {
            showSmsDialog()
        }

        findViewById<View>(R.id.fabChat).setOnClickListener {
            startActivity(Intent(this, DriverChatActivity::class.java))
        }

        findViewById<SlideToActView>(R.id.btnArrived)
            .onSlideCompleteListener = object :
            SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                markArrived()
                view.resetSlider()
            }
        }
        setupFabMenu(findViewById(R.id.fabContainer))
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val slider = findViewById<SlideToActView>(R.id.btnArrived)

        slider.alpha = 0f
        slider.animate()
            .alpha(1f)
            .setDuration(800)
            .start()
        val glow = android.animation.ObjectAnimator.ofFloat(
            slider,
            "alpha",
            1f,
            0.7f,
            1f
        )
        glow.duration = 1500
        glow.repeatCount = android.animation.ValueAnimator.INFINITE
        glow.start()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true
        map.uiSettings.isTiltGesturesEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        getDriverLocation()
    }

    private fun getDriverLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        val fusedLocation = LocationServices
            .getFusedLocationProviderClient(this)

        fusedLocation.lastLocation.addOnSuccessListener { location ->
            location?.let {

                val driverLatLng =
                    LatLng(it.latitude, it.longitude)

                val geocoder = Geocoder(this)
                val list = geocoder.getFromLocationName(trip.pickup, 1)

                if (list == null || list.isEmpty()) {
                    Toast.makeText(this,"Pickup location not found",Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (!list.isNullOrEmpty()) {

                    val pickupLatLng =
                        LatLng(list[0].latitude, list[0].longitude)

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,12f))

                    map.addMarker(
                        MarkerOptions().position(driverLatLng).title("You")
                    )

                    map.addMarker(
                        MarkerOptions().position(pickupLatLng).title("Pickup")
                    )

                    drawRoute(driverLatLng, pickupLatLng)
                }
            }
        }
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {

        val apiKey = BuildConfig.MAPS_API_KEY

        val url =
            "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&key=$apiKey"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body!!.string())

                if (json.getString("status") != "OK") return

                val routes = json.getJSONArray("routes")
                val route = routes.getJSONObject(0)

                val polyline =
                    route.getJSONObject("overview_polyline")
                        .getString("points")

                val decodedPath = PolyUtil.decode(polyline)

                runOnUiThread {

                    map.addPolyline(
                        PolylineOptions()
                            .addAll(decodedPath)
                            .width(10f)
                            .color(Color.BLUE)
                    )

                    val bounds = LatLngBounds.Builder()
                    bounds.include(origin)
                    bounds.include(destination)

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            200
                        )
                    )

                    // Update details box
                    val legs = route.getJSONArray("legs")
                    val leg = legs.getJSONObject(0)

                    val distanceText =
                        leg.getJSONObject("distance").getString("text")

                    val durationText =
                        leg.getJSONObject("duration").getString("text")

                    findViewById<TextView>(R.id.tvDistance).text = distanceText
                    findViewById<TextView>(R.id.tvDuration).text = durationText
                    val detailsText = """

                        Pickup : ${trip.pickup}
                        
                        Drop : ${trip.drop}
                        
                        Pickup Date & Time : ${trip.startTime}
                        
                        Company : ${trip.organisation}
                        
                        Status : ${trip.status ?: "N/A"}
                        """.trimIndent()

                    findViewById<TextView>(R.id.tvTripDetails).text = detailsText
                }
            }
        })
    }

    private fun openGoogleMapsNavigation() {
        val uri = Uri.parse("google.navigation:q=${trip.pickup}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun markArrived() {
        trip.status = "ARRIVED"

        val intent = Intent(this, StartTripOtpActivity::class.java)
        intent.putExtra("trip", trip)
        startActivity(intent)

        finish() // close details screen
    }

    private fun openSmsApp(phone: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$phone") // ✅ safest
            intent.putExtra("sms_body", message)
            startActivity(intent)
        } catch (e: Exception) {
            try {
                // fallback
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("sms:$phone")
                intent.putExtra("sms_body", message)
                startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(this, "SMS not supported on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSmsDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_sms, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val phone = "8431692290"

        dialogView.findViewById<View>(R.id.btnMsg1).setOnClickListener {
            openSmsApp(phone, "I have arrived at pickup location")
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnMsg2).setOnClickListener {
            openSmsApp(phone, "I am on the way, will reach soon")
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnMsg3).setOnClickListener {
            openSmsApp(phone, "Please come to pickup point")
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCustom).setOnClickListener {
            openSmsApp(phone, "")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openDialer(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }

}