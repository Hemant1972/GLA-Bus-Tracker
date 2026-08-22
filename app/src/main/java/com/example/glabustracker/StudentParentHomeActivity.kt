package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StudentParentHomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvAccountType: TextView

    private lateinit var tvBusNumber: TextView
    private lateinit var tvBusStatus: TextView
    private lateinit var tvCurrentLocation: TextView
    private lateinit var tvEta: TextView
    private lateinit var tvNextStop: TextView

    private lateinit var btnTrackBus: Button
    private lateinit var btnSchedule: Button
    private lateinit var btnNotifications: Button

    private lateinit var tvLogout: TextView
    private lateinit var ivProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_parent_home)

        initializeViews()

        loadUserData()

        setupClickListeners()
    }

    // ----------------------------------------------------
    // Initialize Views
    // ----------------------------------------------------

    private fun initializeViews() {

        tvWelcome = findViewById(R.id.tvWelcome)

        tvUserName = findViewById(R.id.tvUserName)

        tvUserId = findViewById(R.id.tvUserId)

        tvAccountType = findViewById(R.id.tvAccountType)

        tvBusNumber = findViewById(R.id.tvBusNumber)

        tvBusStatus = findViewById(R.id.tvBusStatus)

        tvCurrentLocation =
            findViewById(R.id.tvCurrentLocation)

        tvEta = findViewById(R.id.tvEta)

        tvNextStop = findViewById(R.id.tvNextStop)

        btnTrackBus =
            findViewById(R.id.btnTrackBus)

        btnSchedule =
            findViewById(R.id.btnSchedule)

        btnNotifications =
            findViewById(R.id.btnNotifications)

        tvLogout =
            findViewById(R.id.tvLogout)

        ivProfile =
            findViewById(R.id.ivProfile)
    }

    // ----------------------------------------------------
    // Load User Data
    // ----------------------------------------------------

    private fun loadUserData() {

        /*
         * Temporary data.
         *
         * Later these values will come from
         * Firebase / Firestore database.
         */

        val userName =
            intent.getStringExtra("userName")
                ?: "Welcome User"

        val userId =
            intent.getStringExtra("userId")
                ?: "24MCA0000"

        val accountType =
            intent.getStringExtra("accountType")
                ?: "Student"


        tvWelcome.text =
            "Welcome, $userName"

        tvUserName.text =
            userName

        tvUserId.text =
            "Student ID: $userId"

        tvAccountType.text =
            "Account: $accountType"


        // Temporary bus data

        tvBusNumber.text =
            "GLA-01"

        tvBusStatus.text =
            "ON ROUTE"

        tvCurrentLocation.text =
            "Mathura"

        tvEta.text =
            "12 Minutes"

        tvNextStop.text =
            "GLA University"
    }

    // ----------------------------------------------------
    // Click Listeners
    // ----------------------------------------------------

    private fun setupClickListeners() {

        // Track Bus

        btnTrackBus.setOnClickListener {

            Toast.makeText(
                this,
                "Live bus tracking will be available soon",
                Toast.LENGTH_SHORT
            ).show()

            /*
             * Later:
             *
             * Open Google Maps
             * and show live bus location.
             */
        }


        // Bus Schedule

        btnSchedule.setOnClickListener {

            Toast.makeText(
                this,
                "Bus Schedule will open here",
                Toast.LENGTH_SHORT
            ).show()
        }


        // Notifications

        btnNotifications.setOnClickListener {

            Toast.makeText(
                this,
                "No new notifications",
                Toast.LENGTH_SHORT
            ).show()
        }


        // Profile

        ivProfile.setOnClickListener {

            Toast.makeText(
                this,
                "Profile will open here",
                Toast.LENGTH_SHORT
            ).show()
        }


        // Logout

        tvLogout.setOnClickListener {

            logoutUser()
        }
    }

    // ----------------------------------------------------
    // Logout
    // ----------------------------------------------------

    private fun logoutUser() {

        /*
         * Later Firebase logout/session clear
         * will be implemented here.
         */

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