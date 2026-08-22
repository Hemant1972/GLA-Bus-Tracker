package com.example.glabustracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var actLoginType: AutoCompleteTextView
    private lateinit var etUserId: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUp: TextView

    private lateinit var layoutAboutGla: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutInstagram: LinearLayout
    private lateinit var layoutGlams: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initializeViews()
        setupLoginTypeDropdown()
        setupClickListeners()
    }

    private fun initializeViews() {

        actLoginType = findViewById(R.id.actLoginType)
        etUserId = findViewById(R.id.etUserId)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSignUp = findViewById(R.id.tvSignUp)

        layoutAboutGla = findViewById(R.id.layoutAboutGla)
        layoutFacebook = findViewById(R.id.layoutFacebook)
        layoutInstagram = findViewById(R.id.layoutInstagram)
        layoutGlams = findViewById(R.id.layoutGlams)
    }

    private fun setupLoginTypeDropdown() {

        val loginTypes = arrayOf(
            "Student",
            "Parent",
            "Staff"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            loginTypes
        )

        actLoginType.setAdapter(adapter)

        // Student default selected
        actLoginType.setText(loginTypes[0], false)

        actLoginType.setOnClickListener {
            actLoginType.showDropDown()
        }
    }

    private fun setupClickListeners() {

        // Send OTP
        btnSendOtp.setOnClickListener {

            val loginType = actLoginType.text.toString().trim()
            val userId = etUserId.text.toString().trim()

            if (loginType.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select login type",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (userId.isEmpty()) {

                etUserId.error = "Please enter User ID"
                etUserId.requestFocus()

                return@setOnClickListener
            }

            // Temporary functionality
            // Firebase OTP will be added later.

            Toast.makeText(
                this,
                "OTP will be sent to $userId",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Forgot Password
        tvForgotPassword.setOnClickListener {

            Toast.makeText(
                this,
                "Forgot Password clicked",
                Toast.LENGTH_SHORT
            ).show()

            // Later:
            // Open ForgotPasswordActivity
        }


       // Signup click
        tvSignUp.setOnClickListener {

            val intent = Intent(
                this,
                SignupActivity::class.java
            )

            startActivity(intent)
        }

        // Send Otp Click
        btnSendOtp.setOnClickListener {

            val intent = Intent(
                this,
                OtpActivity::class.java
            )

            startActivity(intent)
        }


      // Fogot password click
        val tvForgotPassword =
            findViewById<TextView>(R.id.tvForgotPassword)

        tvForgotPassword.setOnClickListener {

            val intent = Intent(
                this,
                ForgotPasswordActivity::class.java
            )

            startActivity(intent)
        }

        // About GLA
        layoutAboutGla.setOnClickListener {

            openWebsite(
                "https://www.gla.ac.in/"
            )
        }

        // Facebook
        layoutFacebook.setOnClickListener {

            openWebsite(
                "https://www.facebook.com/GLAUniversity/"
            )
        }

        // Instagram
        layoutInstagram.setOnClickListener {

            openWebsite(
                "https://www.instagram.com/glauniversity/"
            )
        }

        // GLAMS
        layoutGlams.setOnClickListener {

            Toast.makeText(
                this,
                "GLAMS will open here",
                Toast.LENGTH_SHORT
            ).show()

            // GLAMS URL will be added after confirming
            // the official GLAMS URL.
        }
    }

    private fun openWebsite(url: String) {

        try {

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )

            startActivity(intent)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to open website",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}