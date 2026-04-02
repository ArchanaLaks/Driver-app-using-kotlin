package com.example.trip_sheet_driver_android.pages.trip

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.trip_sheet_driver_android.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddTripActivity : AppCompatActivity() {

    private lateinit var etStartDate: TextInputEditText
    private lateinit var etEndDate: TextInputEditText
    private lateinit var etPickupTime: TextInputEditText
    private lateinit var etOrganisation: TextInputEditText
    private lateinit var etDutyType: TextInputEditText
    private lateinit var etVehicleType: TextInputEditText
//    private lateinit var etPassenger: AutoCompleteTextView
    private lateinit var etPickupLocation: TextInputEditText
    private lateinit var etDropLocation: TextInputEditText
    private lateinit var etNotes: TextInputEditText
    private lateinit var passengerContainer: LinearLayout
    private lateinit var btnAddPassenger: ImageButton
    private lateinit var btnSubmit: MaterialButton

    private val selectedPassengers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        initViews()
        setupToolbar()
        setupDatePickers()
        setupTimePicker()
        setupPassengerAdd()
        setupSubmit()
    }

    private fun initViews() {
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etPickupTime = findViewById(R.id.etPickupTime)
        etOrganisation = findViewById(R.id.etOrganisation)
        etDutyType = findViewById(R.id.etDutyType)
        etVehicleType = findViewById(R.id.etVehicleType)
//        etPassenger = findViewById(R.id.etPassenger)
        etPickupLocation = findViewById(R.id.etPickupLocation)
        etDropLocation = findViewById(R.id.etDropLocation)
        etNotes = findViewById(R.id.etNotes)
        passengerContainer = findViewById(R.id.passengerContainer)
        btnAddPassenger = findViewById(R.id.btnAddPassenger)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupDatePickers() {

        etStartDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Start Date")
                .build()

            picker.show(supportFragmentManager, "startDate")

            picker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etStartDate.setText(sdf.format(Date
                    (it)))
            }
        }

        etEndDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select End Date")
                .build()

            picker.show(supportFragmentManager, "endDate")

            picker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etEndDate.setText(sdf.format(Date(it)))
            }
        }
    }

    private fun setupTimePicker() {
        etPickupTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build()

            picker.show(supportFragmentManager, "timePicker")

            picker.addOnPositiveButtonClickListener {
                etPickupTime.setText(
                    String.format("%02d:%02d", picker.hour, picker.minute)
                )
            }
        }
    }

    private fun setupPassengerAdd() {

        btnAddPassenger.setOnClickListener {
            addPassengerField()
        }

        // Add first passenger field by default
        addPassengerField()
    }

    private fun addPassengerField() {

        val container = LinearLayout(this)
        container.orientation = LinearLayout.HORIZONTAL
        container.setPadding(0, 16, 0, 16)

        val editText = EditText(this)
        editText.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        editText.hint = "Passenger Name"

        val removeBtn = ImageButton(this)
        removeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        removeBtn.setBackgroundResource(android.R.color.transparent)

        removeBtn.setOnClickListener {
            passengerContainer.removeView(container)
        }

        container.addView(editText)
        container.addView(removeBtn)

        passengerContainer.addView(container)
    }

    private fun setupSubmit() {

        btnSubmit.setOnClickListener {

            val pickupEpoch = combineToEpoch(
                etStartDate.text.toString(),
                etPickupTime.text.toString()
            )

            val dropEpoch = combineToEpoch(
                etEndDate.text.toString(),
                "23:59"
            )

            val passengerNames = mutableListOf<String>()

            for (i in 0 until passengerContainer.childCount) {
                val row = passengerContainer.getChildAt(i) as LinearLayout
                val editText = row.getChildAt(0) as EditText
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    passengerNames.add(name)
                }
            }

            val payload = JSONObject().apply {

                put("startDate", toEpoch(etStartDate.text.toString()))
                put("endDate", toEpoch(etEndDate.text.toString()))
                put("bookingType", 1)

                put("trips", JSONArray().apply {
                    put(JSONObject().apply {

                        put("organisationId", etOrganisation.text.toString().toIntOrNull())
                        put("dutyTypeId", etDutyType.text.toString().toIntOrNull())
                        put("vehicleTypeId", etVehicleType.text.toString().toIntOrNull())
                        put("bookerId", JSONObject.NULL)
                        put("passengerIds", JSONArray(passengerNames))
                        put("pickupTime", pickupEpoch)
                        put("endDate", dropEpoch)
                        put("notes", etNotes.text.toString())

                        put("stops", JSONArray().apply {
                            put(JSONObject().apply {
                                put("sequenceNumber", 1)
                                put("stopType", "PICKUP")
                                put("addressText", etPickupLocation.text.toString())
                            })
                            put(JSONObject().apply {
                                put("sequenceNumber", 2)
                                put("stopType", "DROP")
                                put("addressText", etDropLocation.text.toString())
                            })
                        })
                    })
                })
            }

            println("FINAL PAYLOAD: $payload")
        }
    }

    private fun combineToEpoch(dateStr: String, timeStr: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse("$dateStr $timeStr")
        return date?.time?.div(1000) ?: 0
    }

    private fun toEpoch(dateStr: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr)
        return date?.time?.div(1000) ?: 0
    }
}