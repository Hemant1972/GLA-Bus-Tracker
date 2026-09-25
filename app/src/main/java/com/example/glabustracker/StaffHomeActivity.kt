package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class StaffHomeActivity : AppCompatActivity() {

    private lateinit var layoutLiveTracking: LinearLayout
    private lateinit var layoutRoutes: LinearLayout
    private lateinit var layoutDrivers: LinearLayout
    private lateinit var layoutSchedule: LinearLayout
    private lateinit var layoutNotifications: LinearLayout
    private lateinit var layoutReports: LinearLayout
    private lateinit var btnLogout: Button

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_home)

        // Firebase initialize
        auth = FirebaseAuth.getInstance()

        initializeViews()

        setupClickListeners()
    }

    // ====================================================
    // INITIALIZE VIEWS
    // ====================================================

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

    // ====================================================
    // CLICK LISTENERS
    // ====================================================

    private fun setupClickListeners() {

        // Live Tracking
        layoutLiveTracking.setOnClickListener {

            Toast.makeText(
                this,
                "Live Bus Tracking will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Routes
        layoutRoutes.setOnClickListener {

            Toast.makeText(
                this,
                "Route Management will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Drivers
        layoutDrivers.setOnClickListener {

            Toast.makeText(
                this,
                "Driver Management will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Schedule
        layoutSchedule.setOnClickListener {

            Toast.makeText(
                this,
                "Bus Schedule will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Notifications
        layoutNotifications.setOnClickListener {

            Toast.makeText(
                this,
                "Notifications will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Reports
        layoutReports.setOnClickListener {

            Toast.makeText(
                this,
                "Reports will be added soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Logout
        btnLogout.setOnClickListener {

            logout()
        }
    }

    // ====================================================
    // LOGOUT
    // ====================================================

    private fun logout() {

        /*
         * IMPORTANT:
         *
         * Firebase Authentication session must be
         * cleared before opening LoginActivity.
         *
         * Otherwise LoginActivity.checkUserSession()
         * will detect the Staff Firebase user and
         * automatically open StaffHomeActivity again.
         */

        auth.signOut()

        Toast.makeText(
            this,
            "Logged out successfully",
            Toast.LENGTH_SHORT
        ).show()

        // Open LoginActivity and clear entire Activity stack
        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Tell LoginActivity that this is a fresh logout
        intent.putExtra(
            "loggedOut",
            true
        )

        startActivity(intent)

        finish()
    }
}

