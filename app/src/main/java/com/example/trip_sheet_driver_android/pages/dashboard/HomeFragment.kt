package com.example.trip_sheet_driver_android.pages.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.Trip

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerTrips)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val trips = getTrips()

        val activeTrip = trips.find { it.status == "ACTIVE" }
        val upcomingTrip = trips
            .filter { it.status == "UPCOMING" }
            .minByOrNull { it.startTime }

        val displayList = mutableListOf<Trip>()

        activeTrip?.let { displayList.add(it) }
        if (activeTrip == null) {
            upcomingTrip?.let { displayList.add(it) }
        }

        val emptyView = view.findViewById<TextView>(R.id.tvEmpty)

        if (displayList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            return
        }

        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        adapter = TripAdapter(displayList) { trip ->

            when (trip.status) {

                "UPCOMING" -> {
                    val intent = Intent(requireContext(), TripDetailsActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                }

                "ARRIVED" -> {
                    val intent = Intent(requireContext(), StartTripOtpActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                }

                "ACTIVE" -> {
                    // future use (tracking screen etc)
                    val intent = Intent(requireContext(), TripDetailsActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                }
            }
        }

        recyclerView.adapter = adapter
    }

    private fun getTrips(): List<Trip> {
        return DummyData.tripList
    }
}