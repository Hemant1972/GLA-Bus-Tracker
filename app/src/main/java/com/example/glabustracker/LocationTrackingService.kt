package com.example.glabustracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Build

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class LocationTrackingService : Service() {

    companion object {
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val NOTIFICATION_ID = 1001
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private lateinit var firestore: FirebaseFirestore

    private var assignedBusId: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        firestore = FirebaseFirestore.getInstance()

        locationCallback = object : LocationCallback() {

           override fun onLocationResult(locationResult: LocationResult) {

                val location = locationResult.lastLocation

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    if (assignedBusId.isEmpty()) {
                        Log.e(
                            "LocationTracking",
                            "Bus ID not available"
                        )
                        return
                    }

                    Log.d(
                        "LocationTracking",
                        "Lat: $latitude, Lng: $longitude"
                    )

                    val locationData = hashMapOf(
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "speed" to location.speed.toDouble(),
                        "heading" to location.bearing.toDouble(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                        "status" to "ONLINE"
                    )

                    firestore.collection("busLocations")
                        .document(assignedBusId)
                        .set(locationData)
                        .addOnSuccessListener {
                            Log.d(
                                "LocationTracking",
                                "Location sent to Firebase: $assignedBusId"
                            )
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "LocationTracking",
                                "Firebase location update failed",
                                exception
                            )
                        }
                }
            }
        }
    }



    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        if (
            checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(
                "LocationTracking",
                "Location permission not granted"
            )
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val staffId = intent?.getStringExtra("staffId") ?: ""

        val notification = createNotification()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        if (staffId.isNotEmpty()) {
            fetchAssignedBus(staffId)
        } else {
            Log.e(
                "LocationTracking",
                "Staff ID not available"
            )
        }

        return START_STICKY
    }

//    private fun fetchAssignedBus(staffId: String): Int {
//        TODO("Not yet implemented")
//    }

    private fun fetchAssignedBus(staffId: String) {

        firestore.collection("staff")
            .document(staffId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    assignedBusId =
                        document.getString("busId") ?: ""

                    if (assignedBusId.isNotEmpty()) {

                        Log.d(
                            "LocationTracking",
                            "Assigned Bus: $assignedBusId"
                        )

                        startLocationUpdates()

                    } else {

                        Log.e(
                            "LocationTracking",
                            "No bus assigned to staff"
                        )
                    }

                } else {

                    Log.e(
                        "LocationTracking",
                        "Staff document not found: $staffId"
                    )
                }
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "LocationTracking",
                    "Failed to fetch staff bus",
                    exception
                )
            }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bus Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
            .setContentTitle("GLA Bus Tracker")
            .setContentText("Bus location tracking is active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {

        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )

        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}