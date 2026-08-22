package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StaffHomeActivity : AppCompatActivity() {

    private lateinit var layoutLiveTracking: LinearLayout
    private lateinit var layoutRoutes: LinearLayout
    private lateinit var layoutDrivers: LinearLayout
    private lateinit var layoutSchedule: LinearLayout
    private lateinit var layoutNotifications: LinearLayout
    private lateinit var layoutReports: LinearLayout
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_home)

        initializeViews()

        setupClickListeners()
    }

    private fun initializeViews() {

        layoutLiveTracking =
            findViewById(R.id.layoutLiveTracking)

        layoutRoutes =
            findViewById(R.id.layoutRoutes)

        layoutDrivers =
            findViewById(R.id.layoutDrivers)

        layoutSchedule =
            findViewById(R.id.layoutSchedule)

        layoutNotifications =
            findViewById(R.id.layoutNotifications)

        layoutReports =
            findViewById(R.id.layoutReports)

        btnLogout =
            findViewById(R.id.btnLogout)
    }

    private fun setupClickListeners() {

        layoutLiveTracking.setOnClickListener {

            Toast.makeText(
                this,
                "Live Bus Tracking will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        layoutRoutes.setOnClickListener {

            Toast.makeText(
                this,
                "Route Management will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        layoutDrivers.setOnClickListener {

            Toast.makeText(
                this,
                "Driver Management will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        layoutSchedule.setOnClickListener {

            Toast.makeText(
                this,
                "Bus Schedule will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        layoutNotifications.setOnClickListener {

            Toast.makeText(
                this,
                "Notifications will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        layoutReports.setOnClickListener {

            Toast.makeText(
                this,
                "Reports will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnLogout.setOnClickListener {

            logout()
        }
    }

    private fun logout() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}