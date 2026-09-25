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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

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

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        // Firebase initialization
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupAccountTypeDropdown()
        setupListeners()
    }

    // ----------------------------------------------------
    // Initialize Views
    // ----------------------------------------------------

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

    // ----------------------------------------------------
    // Account Type Dropdown
    // ----------------------------------------------------

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

        // Student selected by default
        actAccountType.setText(
            accountTypes[0],
            false
        )

        actAccountType.setOnClickListener {

            actAccountType.showDropDown()
        }

        actAccountType.setOnItemClickListener { _, _, position, _ ->

            val selectedType = accountTypes[position]

            if (selectedType == "Student") {

                tvMobileLabel.text = "Mobile Number"

                etMobile.hint =
                    "Enter your mobile number"

            } else {

                tvMobileLabel.text =
                    "Parent Mobile Number"

                etMobile.hint =
                    "Enter parent mobile number"
            }
        }
    }

    // ----------------------------------------------------
    // Click Listeners
    // ----------------------------------------------------

    private fun setupListeners() {

        // Sign Up
        btnSignUp.setOnClickListener {

            registerUser()
        }

        // Login
        tvLogin.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }

    // ----------------------------------------------------
    // Register User
    // ----------------------------------------------------

    private fun registerUser() {

        val accountType =
            actAccountType.text.toString().trim()

        val fullName =
            etFullName.text.toString().trim()

        val studentId =
            etStudentId.text.toString().trim()

        val mobile =
            etMobile.text.toString().trim()

        val email =
            etEmail.text.toString().trim()

        val password =
            etPassword.text.toString()

        val confirmPassword =
            etConfirmPassword.text.toString()


        // ------------------------------------------------
        // Account Type Validation
        // ------------------------------------------------

        if (accountType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select account type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ------------------------------------------------
        // Full Name Validation
        // ------------------------------------------------

        if (fullName.isEmpty()) {

            etFullName.error =
                "Enter your full name"

            etFullName.requestFocus()

            return
        }


        // ------------------------------------------------
        // Student ID Validation
        // ------------------------------------------------

        if (studentId.isEmpty()) {

            etStudentId.error =
                "Enter Student ID"

            etStudentId.requestFocus()

            return
        }


        // ------------------------------------------------
        // Mobile Validation
        // ------------------------------------------------

        if (mobile.isEmpty()) {

            etMobile.error =
                "Enter mobile number"

            etMobile.requestFocus()

            return
        }

        if (mobile.length != 10) {

            etMobile.error =
                "Enter valid 10 digit mobile number"

            etMobile.requestFocus()

            return
        }


        // ------------------------------------------------
        // Email Validation
        // ------------------------------------------------

        if (email.isEmpty()) {

            etEmail.error =
                "Enter email address"

            etEmail.requestFocus()

            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etEmail.error =
                "Enter valid email address"

            etEmail.requestFocus()

            return
        }


        // ------------------------------------------------
        // Password Validation
        // ------------------------------------------------

        if (password.isEmpty()) {

            etPassword.error =
                "Create a password"

            etPassword.requestFocus()

            return
        }

        if (password.length < 6) {

            etPassword.error =
                "Password must contain at least 6 characters"

            etPassword.requestFocus()

            return
        }


        // ------------------------------------------------
        // Confirm Password Validation
        // ------------------------------------------------

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


        // ------------------------------------------------
        // Disable Button
        // ------------------------------------------------

        btnSignUp.isEnabled = false

        btnSignUp.text = "Creating Account..."


        // ------------------------------------------------
        // Firebase Authentication
        // ------------------------------------------------

        firebaseAuth
            .createUserWithEmailAndPassword(
                email,
                password
            )
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val firebaseUser =
                        firebaseAuth.currentUser

                    if (firebaseUser == null) {

                        showSignupError(
                            "Unable to create user"
                        )

                        return@addOnCompleteListener
                    }

                    val uid =
                        firebaseUser.uid

                    saveUserToFirestore(
                        uid = uid,
                        accountType = accountType,
                        fullName = fullName,
                        studentId = studentId,
                        mobile = mobile,
                        email = email
                    )

                } else {

                    btnSignUp.isEnabled = true
                    btnSignUp.text = "SIGN UP"

                    val errorMessage =
                        task.exception?.message
                            ?: "Registration failed"

                    Toast.makeText(
                        this,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ----------------------------------------------------
    // Save User To Firestore
    // ----------------------------------------------------

    private fun saveUserToFirestore(
        uid: String,
        accountType: String,
        fullName: String,
        studentId: String,
        mobile: String,
        email: String
    ) {

        // Common users document
        val userData = hashMapOf(

            "fullName" to fullName,

            "email" to email,

            "role" to accountType.lowercase(),

            "studentId" to studentId,

            "mobile" to mobile,

            "createdAt" to FieldValue.serverTimestamp()
        )


        // ------------------------------------------------
        // users/{UID}
        // ------------------------------------------------

        firestore
            .collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {

                // Now save role-specific data

                if (accountType == "Student") {

                    saveStudentData(
                        uid = uid,
                        fullName = fullName,
                        studentId = studentId,
                        mobile = mobile,
                        email = email
                    )

                } else {

                    saveParentData(
                        uid = uid,
                        fullName = fullName,
                        studentId = studentId,
                        mobile = mobile,
                        email = email
                    )
                }
            }
            .addOnFailureListener { exception ->

                showFirestoreError(
                    exception.message
                        ?: "Failed to save user data"
                )
            }
    }

    // ----------------------------------------------------
    // Save Student Data
    // ----------------------------------------------------

    private fun saveStudentData(
        uid: String,
        fullName: String,
        studentId: String,
        mobile: String,
        email: String
    ) {

        val studentData = hashMapOf(

            "uid" to uid,

            "fullName" to fullName,

            "email" to email,

            "mobile" to mobile,

            "studentId" to studentId,

            // Bus information will be assigned later
            "busId" to "",

            "routeId" to "",

            "stopId" to "",

            "createdAt" to FieldValue.serverTimestamp()
        )


        firestore
            .collection("students")
            .document(studentId)
            .set(studentData)
            .addOnSuccessListener {

                registrationSuccessful()
            }
            .addOnFailureListener { exception ->

                showFirestoreError(
                    exception.message
                        ?: "Failed to save student data"
                )
            }
    }

    // ----------------------------------------------------
    // Save Parent Data
    // ----------------------------------------------------

    private fun saveParentData(
        uid: String,
        fullName: String,
        studentId: String,
        mobile: String,
        email: String
    ) {

        val parentData = hashMapOf(

            "uid" to uid,

            "fullName" to fullName,

            "email" to email,

            "parentMobile" to mobile,

            // Child's Student ID
            "studentId" to studentId,

            "role" to "parent",

            "createdAt" to FieldValue.serverTimestamp()
        )


        firestore
            .collection("parents")
            .document(uid)
            .set(parentData)
            .addOnSuccessListener {

                registrationSuccessful()
            }
            .addOnFailureListener { exception ->

                showFirestoreError(
                    exception.message
                        ?: "Failed to save parent data"
                )
            }
    }

    // ----------------------------------------------------
    // Registration Successful
    // ----------------------------------------------------

    private fun registrationSuccessful() {

        Toast.makeText(
            this,
            "Account created successfully!",
            Toast.LENGTH_LONG
        ).show()


        // Firebase user ko logout kar dete hain
        // because actual Login ab LoginActivity se hoga.
        firebaseAuth.signOut()


        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        startActivity(intent)

        finish()
    }

    // ----------------------------------------------------
    // Signup Error
    // ----------------------------------------------------

    private fun showSignupError(
        message: String
    ) {

        btnSignUp.isEnabled = true
        btnSignUp.text = "SIGN UP"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    // ----------------------------------------------------
    // Firestore Error
    // ----------------------------------------------------

    private fun showFirestoreError(
        message: String
    ) {

        btnSignUp.isEnabled = true
        btnSignUp.text = "SIGN UP"

        Toast.makeText(
            this,
            "Database error: $message",
            Toast.LENGTH_LONG
        ).show()
    }
}