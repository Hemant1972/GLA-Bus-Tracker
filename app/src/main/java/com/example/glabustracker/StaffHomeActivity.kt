package com.example.glabustracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView

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

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_home)

        // Firebase initialize
        auth = FirebaseAuth.getInstance()

        // Location initialize
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        initializeViews()
        setupClickListeners()
    }

    // ====================================================
    // INITIALIZE VIEWS
    // ====================================================

    private fun initializeViews() {
        tvLocation = findViewById(R.id.tvLocation)

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
            checkLocationPermission()
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
    // LOCATION PERMISSION
    // ====================================================

    private fun checkLocationPermission() {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {

            getCurrentLocation()

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // ====================================================
    // GET CURRENT GPS LOCATION
    // ====================================================

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    tvLocation.text = "Latitude: $latitude\nLongitude: $longitude"

                } else {

                    Toast.makeText(
                        this,
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Location error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ====================================================
    // LOCATION PERMISSION RESULT
    // ====================================================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() &&
                grantResults.any {
                    it == PackageManager.PERMISSION_GRANTED
                }
            ) {

                getCurrentLocation()

            } else {

                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ====================================================
    // LOGOUT
    // ====================================================

    private fun logout() {

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

        intent.putExtra(
            "loggedOut",
            true
        )

        startActivity(intent)

        finish()
    }
}