package com.example.trip_sheet_driver_android.pages.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.data.model.Trip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripAdapter(private val list: List<Trip>,
                  private val onClick: (Trip) -> Unit) :
    RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTripNumber: TextView = view.findViewById(R.id.tvTripNumber)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvPassenger: TextView = view.findViewById(R.id.tvPassenger)
        val tvOrganisation: TextView = view.findViewById(R.id.tvOrganisation)
        val tvPickup: TextView = view.findViewById(R.id.tvPickup)
        val tvDrop: TextView = view.findViewById(R.id.tvDrop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = list[position]

        holder.itemView.setOnClickListener {
            onClick(trip)
        }

        holder.tvTripNumber.text = "Trip #${trip.id}"
        holder.tvStatus.text = trip.status
        holder.tvDateTime.text = formatDateTime(trip.startTime)
        holder.tvPassenger.text = trip.passenger
        holder.tvOrganisation.text = trip.organisation
        holder.tvPickup.text = trip.pickup
        holder.tvDrop.text = trip.drop

        setStatusStyle(holder, trip.status)

    }

    private fun setStatusStyle(holder: ViewHolder, status: String) {
        when (status) {
            "ACTIVE" -> {
                holder.tvStatus.text = "ACTIVE"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green)
            }
            "UPCOMING" -> {
                holder.tvStatus.text = "UPCOMING"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue)
            }
        }
    }
    private fun formatDateTime(epoch: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        return sdf.format(Date(epoch * 1000)) // convert seconds to millis
    }
}

object DummyData {
    val tripList = listOf(
        Trip(1, "John Doe", "ABC Corp", "Kempegowda International Airport", "Otera Hotel", 1700000000, "ACTIVE"),
        Trip(2, "Ravi Kumar", "XYZ Ltd", "Office", "Mall", 1800000000, "UPCOMING")
    )
}