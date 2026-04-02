package com.example.trip_sheet_driver_android.pages.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trip_sheet_driver_android.R

class TripsFragment : Fragment(R.layout.fragment_trips) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerTrips)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

         adapter = TripAdapter(DummyData.tripList) { trip ->
            val intent = Intent(requireContext(), TripDetailsActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}