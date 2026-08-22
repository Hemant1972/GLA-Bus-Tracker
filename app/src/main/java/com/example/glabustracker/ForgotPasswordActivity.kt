package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var actAccountType: AutoCompleteTextView
    private lateinit var etStudentId: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var tvBackToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        initializeViews()
        setupAccountTypeDropdown()
        setupListeners()
    }

    private fun initializeViews() {

        actAccountType = findViewById(R.id.actAccountType)
        etStudentId = findViewById(R.id.etStudentId)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
    }

    private fun setupAccountTypeDropdown() {

        val accountTypes = arrayOf(
            "Student",
            "Parent"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            accountTypes
        )

        actAccountType.setAdapter(adapter)

        // Student as default option
        actAccountType.setText(
            accountTypes[0],
            false
        )

        actAccountType.setOnClickListener {
            actAccountType.showDropDown()
        }
    }

    private fun setupListeners() {

        btnSendOtp.setOnClickListener {

            sendOtp()
        }

        tvBackToLogin.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }

    private fun sendOtp() {

        val accountType =
            actAccountType.text.toString().trim()

        val studentId =
            etStudentId.text.toString().trim()

        // Account type validation

        if (accountType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select account type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Student ID validation

        if (studentId.isEmpty()) {

            etStudentId.error =
                "Enter Student ID"

            etStudentId.requestFocus()

            return
        }

        /*
         * Firebase / Firestore logic
         * will be implemented here.
         *
         * Student:
         * Student ID → Student mobile number
         *
         * Parent:
         * Student ID → Parent mobile number
         */

        Toast.makeText(
            this,
            "Finding registered mobile number...",
            Toast.LENGTH_SHORT
        ).show()

        /*
         * Temporary navigation to OTP page.
         */

        val intent = Intent(
            this,
            OtpActivity::class.java
        )

        intent.putExtra(
            "accountType",
            accountType
        )

        intent.putExtra(
            "studentId",
            studentId
        )

        startActivity(intent)
    }
}