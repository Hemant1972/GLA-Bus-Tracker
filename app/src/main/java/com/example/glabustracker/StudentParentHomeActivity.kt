package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_parent_home)

        initializeViews()

        // Firebase initialize
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
    // Load User Data From Firebase
    // ----------------------------------------------------

    private fun loadUserData() {

        val currentUser = auth.currentUser

        // Check whether user is logged in
        if (currentUser == null) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            goToLogin()

            return
        }

        val uid = currentUser.uid

        /*
         * Firestore structure:
         *
         * users
         *   └── UID
         *       ├── fullName
         *       ├── email
         *       ├── mobile
         *       ├── role
         *       ├── studentId
         *       └── createdAt
         */

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val userName =
                        document.getString("fullName")
                            ?: "User"

                    val studentId =
                        document.getString("studentId")
                            ?: "Not Available"

                    val role =
                        document.getString("role")
                            ?: ""

                    // ------------------------------------
                    // Check Role
                    // ------------------------------------

                    when (role.lowercase()) {

                        "student" -> {

                            tvWelcome.text =
                                "Welcome, $userName"

                            tvUserName.text =
                                userName

                            tvUserId.text =
                                "Student ID: $studentId"

                            tvAccountType.text =
                                "Account: Student"
                        }

                        "parent" -> {

                            tvWelcome.text =
                                "Welcome, $userName"

                            tvUserName.text =
                                userName

                            tvUserId.text =
                                "Student ID: $studentId"

                            tvAccountType.text =
                                "Account: Parent"
                        }

                        else -> {

                            Toast.makeText(
                                this,
                                "Invalid account role.",
                                Toast.LENGTH_SHORT
                            ).show()

                            goToLogin()
                        }
                    }

                } else {

                    Toast.makeText(
                        this,
                        "User profile not found.",
                        Toast.LENGTH_SHORT
                    ).show()

                    goToLogin()
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Unable to load user data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }


        // -----------------------------------------------
        // Temporary Bus Data
        // -----------------------------------------------

        /*
         * These values will later come from Firestore
         * / Realtime Database / Live GPS.
         */

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

        // ------------------------------------------------
        // Track Bus
        // ------------------------------------------------

        btnTrackBus.setOnClickListener {

            Toast.makeText(
                this,
                "Live bus tracking will be available soon",
                Toast.LENGTH_SHORT
            ).show()

            /*
             * Later:
             *
             * Google Maps
             * +
             * Live bus location
             */
        }


        // ------------------------------------------------
        // Bus Schedule
        // ------------------------------------------------

        btnSchedule.setOnClickListener {

            Toast.makeText(
                this,
                "Bus Schedule will open here",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ------------------------------------------------
        // Notifications
        // ------------------------------------------------

        btnNotifications.setOnClickListener {

            Toast.makeText(
                this,
                "No new notifications",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ------------------------------------------------
        // Profile
        // ------------------------------------------------

        ivProfile.setOnClickListener {

            Toast.makeText(
                this,
                "Profile will open here",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ------------------------------------------------
        // Logout
        // ------------------------------------------------

        tvLogout.setOnClickListener {

            logoutUser()
        }
    }

    // ----------------------------------------------------
    // Logout
    // ----------------------------------------------------

    private fun logoutUser() {

        /*
         * Firebase Authentication session clear
         */

        auth.signOut()

        Toast.makeText(
            this,
            "Logged out successfully",
            Toast.LENGTH_SHORT
        ).show()

        goToLogin()
    }

    // ----------------------------------------------------
    // Go To Login
    // ----------------------------------------------------

    private fun goToLogin() {

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