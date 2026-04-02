package com.example.trip_sheet_driver_android.pages.dashboard

import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.animation.LinearInterpolator
import com.example.trip_sheet_driver_android.BuildConfig
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.Trip
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.PolyUtil
import com.ncorti.slidetoact.SlideToActView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.math.*

class TripNavigationActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var trip: Trip? = null
    private var carMarker: Marker? = null
    private var dropMarker: Marker? = null
    private var routePolyline: Polyline? = null

    private var isFollowing = true
    private val client = OkHttpClient()
    private var routePoints: List<LatLng> = listOf()

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_navigation_drop)

        trip = intent.getParcelableExtra("trip")
        if (trip == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvDrop).text = "Drop: ${trip?.drop}"

        findViewById<MaterialToolbar>(R.id.toolbarNav)
            .setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnNavigate)
            .setOnClickListener { openGoogleMaps() }

        findViewById<FloatingActionButton>(R.id.btnRecenter)
            .setOnClickListener {

                isFollowing = true

                val marker = carMarker
                val googleMap = map

                if (marker != null && googleMap != null) {
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(marker.position)
                                .zoom(17f)
                                .bearing(marker.rotation)
                                .tilt(45f)
                                .build()
                        )
                    )
                }
            }

        val slider = findViewById<SlideToActView>(R.id.btnCompleteTrip)

        slider.onSlideCompleteListener =
            object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    val intent = Intent(this@TripNavigationActivity, TripSignatureActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                    finish()
                }
            }

        slider.alpha = 0f
        slider.animate().alpha(1f).setDuration(800).start()

        val glow = android.animation.ObjectAnimator.ofFloat(
            slider, "alpha", 1f, 0.7f, 1f
        )
        glow.duration = 1500
        glow.repeatCount = ValueAnimator.INFINITE
        glow.start()

        // ✅ SAFE MAP INIT
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapNav) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL

            uiSettings.apply {
                isZoomGesturesEnabled = true
                isScrollGesturesEnabled = true
                isTiltGesturesEnabled = true
                isRotateGesturesEnabled = true

                isCompassEnabled = false
                isMapToolbarEnabled = false
                isZoomControlsEnabled = false
                isMyLocationButtonEnabled = false
            }

            isBuildingsEnabled = true
            isTrafficEnabled = true
        }

        map?.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                isFollowing = false
            }
        }

        startLiveTracking()
    }

    private fun startLiveTracking() {
        handler.post(object : Runnable {
            override fun run() {
                getRouteAndUpdate()
                handler.postDelayed(this, 5000)
            }
        })
    }

    private fun getRouteAndUpdate() {

        val fusedLocation =
            LocationServices.getFusedLocationProviderClient(this)

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocation.lastLocation.addOnSuccessListener { loc ->

            if (loc == null || trip?.drop.isNullOrEmpty()) return@addOnSuccessListener

            val origin = LatLng(loc.latitude, loc.longitude)

            val geo = Geocoder(this)
            val list = try {
                geo.getFromLocationName(trip!!.drop!!, 1)
            } catch (e: Exception) {
                null
            }

            if (list.isNullOrEmpty()) return@addOnSuccessListener

            val destination = LatLng(list[0].latitude, list[0].longitude)

            fetchRoute(origin, destination)
        }
    }

    private fun fetchRoute(origin: LatLng, destination: LatLng) {

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

                val body = response.body?.string() ?: return
                val json = JSONObject(body)

                if (json.optString("status") != "OK") return

                val routes = json.optJSONArray("routes") ?: return
                if (routes.length() == 0) return

                val route = routes.getJSONObject(0)

                val polyline =
                    route.getJSONObject("overview_polyline")
                        .getString("points")

                routePoints = PolyUtil.decode(polyline)

                val leg = route.getJSONArray("legs").getJSONObject(0)

                val distance =
                    leg.getJSONObject("distance").getString("text")

                val duration =
                    leg.getJSONObject("duration").getString("text")

                runOnUiThread {
                    drawRoute(origin, destination)
                    updateUI(distance, duration)
                }
            }
        })
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {

        val googleMap = map ?: return
        if (routePoints.isEmpty()) return

        // ROUTE
        if (routePolyline == null) {
            routePolyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(routePoints)
                    .width(10f)
                    .color(android.graphics.Color.BLUE)
            )
        } else {
            routePolyline?.points = routePoints
        }

        // DROP MARKER
        if (dropMarker == null) {
            dropMarker = googleMap.addMarker(
                MarkerOptions().position(destination).title("Drop")
            )
        }

        // CAR MARKER
        val icon = getResizedCarIcon()

        if (carMarker == null) {
            carMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(origin)
                    .flat(true)
                    .icon(icon)
                    .anchor(0.5f, 0.5f)
            )
        } else {
            animateCar(carMarker!!.position, origin)
        }

        // CAMERA
        if (isFollowing && carMarker != null) {
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(origin)
                        .zoom(17f)
                        .bearing(carMarker!!.rotation)
                        .tilt(45f)
                        .build()
                )
            )
        }
    }

    private fun updateUI(distance: String, duration: String) {
        findViewById<TextView>(R.id.tvDistanceNav).text = distance
        findViewById<TextView>(R.id.tvEtaNav).text = duration
    }

    private fun openGoogleMaps() {
        val uri = "google.navigation:q=${trip?.drop}".toUri()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun animateCar(start: LatLng, end: LatLng) {

        val marker = carMarker ?: return

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener {
            val fraction = it.animatedFraction

            val lat = start.latitude + (end.latitude - start.latitude) * fraction
            val lng = start.longitude + (end.longitude - start.longitude) * fraction

            val newPos = LatLng(lat, lng)
            marker.position = newPos
        }

        animator.start()
    }

    private fun getResizedCarIcon(): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.car)
        val scaled = Bitmap.createScaledBitmap(bitmap, 60, 60, false)
        return BitmapDescriptorFactory.fromBitmap(scaled)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}