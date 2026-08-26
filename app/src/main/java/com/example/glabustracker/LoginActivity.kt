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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var actLoginType: AutoCompleteTextView
    private lateinit var etUserId: EditText
    private lateinit var etPassword: EditText

    private lateinit var btnLogin: Button
    private lateinit var tvLoginWithOtp: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUp: TextView

    private lateinit var layoutAboutGla: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutInstagram: LinearLayout
    private lateinit var layoutGlams: LinearLayout

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Firebase initialize
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupLoginTypeDropdown()
        setupClickListeners()
    }

    // ----------------------------------------------------
    // Initialize Views
    // ----------------------------------------------------

    private fun initializeViews() {

        actLoginType = findViewById(R.id.actLoginType)

        etUserId = findViewById(R.id.etUserId)

        etPassword = findViewById(R.id.etPassword)

        btnLogin = findViewById(R.id.btnLogin)

        tvLoginWithOtp = findViewById(R.id.tvLoginWithOtp)

        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        tvSignUp = findViewById(R.id.tvSignUp)

        layoutAboutGla = findViewById(R.id.layoutAboutGla)

        layoutFacebook = findViewById(R.id.layoutFacebook)

        layoutInstagram = findViewById(R.id.layoutInstagram)

        layoutGlams = findViewById(R.id.layoutGlams)
    }

    // ----------------------------------------------------
    // Login Type Dropdown
    // ----------------------------------------------------

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

        // Student selected by default
        actLoginType.setText(
            loginTypes[0],
            false
        )

        actLoginType.setOnClickListener {

            actLoginType.showDropDown()
        }
    }

    // ----------------------------------------------------
    // Click Listeners
    // ----------------------------------------------------

    private fun setupClickListeners() {

        // NORMAL LOGIN
        btnLogin.setOnClickListener {

            loginUser()
        }

        // LOGIN WITH OTP
        tvLoginWithOtp.setOnClickListener {

            loginWithOtp()
        }

        // FORGOT PASSWORD
        tvForgotPassword.setOnClickListener {

            val intent = Intent(
                this,
                ForgotPasswordActivity::class.java
            )

            startActivity(intent)
        }

        // SIGN UP
        tvSignUp.setOnClickListener {

            val intent = Intent(
                this,
                SignupActivity::class.java
            )

            startActivity(intent)
        }

        // ABOUT GLA
        layoutAboutGla.setOnClickListener {

            openWebsite(
                "https://www.gla.ac.in/"
            )
        }

        // FACEBOOK
        layoutFacebook.setOnClickListener {

            openWebsite(
                "https://www.facebook.com/GLAUniversity/"
            )
        }

        // INSTAGRAM
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

            // Official GLAMS URL will be added later.
        }
    }

    // ----------------------------------------------------
    // Firebase Login
    // ----------------------------------------------------

    private fun loginUser() {

        val loginType =
            actLoginType.text.toString().trim()

        val userId =
            etUserId.text.toString().trim()

        val password =
            etPassword.text.toString()

        // ------------------------------------------------
        // Validate Login Type
        // ------------------------------------------------

        if (loginType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select login type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ------------------------------------------------
        // Validate User ID
        // ------------------------------------------------

        if (userId.isEmpty()) {

            etUserId.error =
                "Please enter User ID"

            etUserId.requestFocus()

            return
        }

        // ------------------------------------------------
        // Validate Password
        // ------------------------------------------------

        if (password.isEmpty()) {

            etPassword.error =
                "Please enter password"

            etPassword.requestFocus()

            return
        }

        btnLogin.isEnabled = false

        Toast.makeText(
            this,
            "Checking account...",
            Toast.LENGTH_SHORT
        ).show()

        /*
         * IMPORTANT:
         * Firestore Rules require request.auth != null.
         *
         * The old code tried to read /users BEFORE Firebase
         * Authentication, which caused:
         *
         * PERMISSION_DENIED
         *
         * To keep your current Firestore Rules secure, we use
         * Firebase Anonymous Authentication only for the
         * initial studentId -> email lookup.
         *
         * Then we sign out the anonymous user and perform the
         * real email/password login.
         *
         * Firebase Console:
         * Authentication -> Sign-in method -> Anonymous -> Enable
         */

        val currentUser = auth.currentUser

        if (currentUser == null) {

            auth.signInAnonymously()
                .addOnSuccessListener {
                    findUserAndAuthenticate(
                        loginType,
                        userId,
                        password
                    )
                }
                .addOnFailureListener { exception ->

                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Unable to access account database: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

        } else {

            // If an anonymous session already exists, it is
            // already authenticated and can read Firestore.
            findUserAndAuthenticate(
                loginType,
                userId,
                password
            )
        }
    }

    // ----------------------------------------------------
    // Find User ID and Continue With Firebase Authentication
    // ----------------------------------------------------

    private fun findUserAndAuthenticate(
        loginType: String,
        userId: String,
        password: String
    ) {

        firestore.collection("users")
            .whereEqualTo("studentId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "User ID not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val document = documents.documents[0]

                val email =
                    document.getString("email")

                val storedRole =
                    document.getString("role")

                if (email.isNullOrBlank()) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Email not found for this account",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                if (storedRole.isNullOrBlank()) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "User role not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Check selected login type with Firestore role
                // ------------------------------------------------

                if (!storedRole.equals(loginType, ignoreCase = true)) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Incorrect account type selected",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                /*
                 * The Firestore lookup was performed with an
                 * anonymous authenticated session. Now remove
                 * that temporary session before the real login.
                 */
                auth.signOut()

                // ------------------------------------------------
                // Real Firebase Authentication
                // ------------------------------------------------

                auth.signInWithEmailAndPassword(
                    email.trim(),
                    password
                )
                    .addOnSuccessListener {

                        val firebaseUser =
                            auth.currentUser

                        if (firebaseUser == null) {

                            btnLogin.isEnabled = true

                            Toast.makeText(
                                this,
                                "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@addOnSuccessListener
                        }

                        // Verify the authenticated user's profile.
                        verifyUserRole(
                            firebaseUser.uid,
                            loginType,
                            userId
                        )
                    }
                    .addOnFailureListener { exception ->

                        btnLogin.isEnabled = true

                        Toast.makeText(
                            this,
                            getLoginErrorMessage(
                                exception.message
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { exception ->

                auth.signOut()
                btnLogin.isEnabled = true

                Toast.makeText(
                    this,
                    "Database error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ----------------------------------------------------
    // Verify UID and Role
    // ----------------------------------------------------

    private fun verifyUserRole(
        uid: String,
        loginType: String,
        userId: String
    ) {

        /*
         * Do not assume that users/{uid} exists.
         * We verify the profile using the same studentId that
         * was used during login.
         *
         * This works whether Signup stored the Firestore
         * document with the Firebase UID or with an auto-ID.
         */

        firestore.collection("users")
            .whereEqualTo("studentId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "User profile not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val document = documents.documents[0]

                val role =
                    document.getString("role")

                val profileEmail =
                    document.getString("email")

                val authenticatedEmail =
                    auth.currentUser?.email

                if (role.isNullOrBlank()) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "User role not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Verify role
                // ------------------------------------------------

                if (!role.equals(loginType, ignoreCase = true)) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Account role verification failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Verify that the Firestore email belongs to the
                // Firebase Authentication account.
                // ------------------------------------------------

                if (
                    profileEmail.isNullOrBlank() ||
                    authenticatedEmail.isNullOrBlank() ||
                    !profileEmail.trim().equals(
                        authenticatedEmail.trim(),
                        ignoreCase = true
                    )
                ) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Account profile does not match the authenticated account",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Optional UID verification
                // ------------------------------------------------
                // If Signup saved a "uid" field, verify it.
                // If the field does not exist, do not fail login.
                val storedUid =
                    document.getString("uid")

                if (
                    !storedUid.isNullOrBlank() &&
                    storedUid != uid
                ) {

                    auth.signOut()
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Account security verification failed",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Login successful
                // ------------------------------------------------

                openCorrectHome(
                    loginType,
                    userId,
                    document.getString("fullName")
                        ?: "User"
                )
            }
            .addOnFailureListener { exception ->

                auth.signOut()
                btnLogin.isEnabled = true

                Toast.makeText(
                    this,
                    "Unable to verify account: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ----------------------------------------------------
    // Open Correct Home Page
    // ----------------------------------------------------

    private fun openCorrectHome(
        loginType: String,
        userId: String,
        userName: String
    ) {

        val intent: Intent

        if (loginType.equals(
                "Student",
                ignoreCase = true
            )
        ) {

            // Student and Parent currently
            // use the same portal
            intent = Intent(
                this,
                StudentParentHomeActivity::class.java
            )

        } else if (
            loginType.equals(
                "Parent",
                ignoreCase = true
            )
        ) {

            // Parent also uses same portal
            intent = Intent(
                this,
                StudentParentHomeActivity::class.java
            )

        } else {

            // Staff
            intent = Intent(
                this,
                StaffHomeActivity::class.java
            )
        }

        intent.putExtra(
            "userName",
            userName
        )

        intent.putExtra(
            "userId",
            userId
        )

        intent.putExtra(
            "accountType",
            loginType
        )

        startActivity(intent)

        finish()
    }

    // ----------------------------------------------------
    // Firebase Login Error Messages
    // ----------------------------------------------------

    private fun getLoginErrorMessage(
        errorMessage: String?
    ): String {

        return when {

            errorMessage?.contains(
                "password is invalid",
                ignoreCase = true
            ) == true -> {

                "Incorrect password"
            }

            errorMessage?.contains(
                "no user record",
                ignoreCase = true
            ) == true -> {

                "Account not found"
            }

            errorMessage?.contains(
                "user-disabled",
                ignoreCase = true
            ) == true -> {

                "This account has been disabled"
            }

            errorMessage?.contains(
                "network",
                ignoreCase = true
            ) == true -> {

                "Network error. Check your internet connection"
            }

            else -> {

                "Login failed. Please check your User ID and password"
            }
        }
    }

    // ----------------------------------------------------
    // Login With OTP
    // ----------------------------------------------------

    private fun loginWithOtp() {

        val loginType =
            actLoginType.text.toString().trim()

        val userId =
            etUserId.text.toString().trim()

        // Check Login Type

        if (loginType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select login type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Check User ID

        if (userId.isEmpty()) {

            etUserId.error =
                "Please enter User ID"

            etUserId.requestFocus()

            return
        }

        val intent = Intent(
            this,
            OtpActivity::class.java
        )

        intent.putExtra(
            "purpose",
            "login"
        )

        intent.putExtra(
            "loginType",
            loginType
        )

        intent.putExtra(
            "userId",
            userId
        )

        startActivity(intent)
    }

    // ----------------------------------------------------
    // Open Website
    // ----------------------------------------------------

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
