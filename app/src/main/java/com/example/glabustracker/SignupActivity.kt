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

class SignupActivity : AppCompatActivity() {

    private lateinit var actAccountType: AutoCompleteTextView

    private lateinit var etFullName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var tvMobileLabel: TextView
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        initializeViews()
        setupAccountTypeDropdown()
        setupListeners()
    }

    private fun initializeViews() {

        actAccountType = findViewById(R.id.actAccountType)

        etFullName = findViewById(R.id.etFullName)
        etStudentId = findViewById(R.id.etStudentId)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        tvMobileLabel = findViewById(R.id.tvMobileLabel)

        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)
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

        // Student default
        actAccountType.setText(accountTypes[0], false)

        actAccountType.setOnClickListener {
            actAccountType.showDropDown()
        }

        actAccountType.setOnItemClickListener { _, _, position, _ ->

            val selectedType = accountTypes[position]

            if (selectedType == "Student") {

                tvMobileLabel.text = "Mobile Number"
                etMobile.hint = "Enter your mobile number"

            } else {

                tvMobileLabel.text = "Parent Mobile Number"
                etMobile.hint = "Enter parent mobile number"
            }
        }
    }

    private fun setupListeners() {

        btnSignUp.setOnClickListener {

            registerUser()
        }

        tvLogin.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }

    private fun registerUser() {

        val accountType = actAccountType.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val studentId = etStudentId.text.toString().trim()
        val mobile = etMobile.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Account type validation

        if (accountType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select account type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Name validation

        if (fullName.isEmpty()) {

            etFullName.error = "Enter your full name"
            etFullName.requestFocus()

            return
        }

        // Student ID validation

        if (studentId.isEmpty()) {

            etStudentId.error = "Enter Student ID"
            etStudentId.requestFocus()

            return
        }

        // Mobile validation

        if (mobile.isEmpty()) {

            etMobile.error = "Enter mobile number"
            etMobile.requestFocus()

            return
        }

        if (mobile.length != 10) {

            etMobile.error = "Enter valid 10 digit mobile number"
            etMobile.requestFocus()

            return
        }

        // Email validation

        if (email.isEmpty()) {

            etEmail.error = "Enter email address"
            etEmail.requestFocus()

            return
        }

        // Password validation

        if (password.isEmpty()) {

            etPassword.error = "Create a password"
            etPassword.requestFocus()

            return
        }

        if (password.length < 6) {

            etPassword.error =
                "Password must contain at least 6 characters"

            etPassword.requestFocus()

            return
        }

        // Confirm password

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.error =
                "Confirm your password"

            etConfirmPassword.requestFocus()

            return
        }

        if (password != confirmPassword) {

            etConfirmPassword.error =
                "Passwords do not match"

            etConfirmPassword.requestFocus()

            return
        }

        /*
         * Firebase registration will be implemented here.
         *
         * Student:
         * role = student
         *
         * Parent:
         * role = parent
         * studentId = child student ID
         * mobile = parent mobile
         */

        Toast.makeText(
            this,
            "$accountType account details validated successfully",
            Toast.LENGTH_SHORT
        ).show()

        // Temporary navigation to Login
        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        startActivity(intent)
        finish()
    }
}