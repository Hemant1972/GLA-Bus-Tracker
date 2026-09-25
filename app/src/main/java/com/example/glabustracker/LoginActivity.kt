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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    // ----------------------------------------------------
    // Views
    // ----------------------------------------------------

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

    // ----------------------------------------------------
    // Firebase
    // ----------------------------------------------------

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Prevent duplicate session checking
    private var sessionCheckCompleted = false


    // ====================================================
    // ON CREATE
    // ====================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI first
        initializeViews()

        setupLoginTypeDropdown()

        setupClickListeners()

        // Then check existing session
        checkUserSession()
    }


    // ====================================================
    // INITIALIZE VIEWS
    // ====================================================

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


    // ====================================================
    // LOGIN TYPE DROPDOWN
    // ====================================================

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

        // Default = Student
        actLoginType.setText(
            loginTypes[0],
            false
        )

        actLoginType.setOnClickListener {
            actLoginType.showDropDown()
        }
    }


    // ====================================================
    // CLICK LISTENERS
    // ====================================================

    private fun setupClickListeners() {

        // Normal Login
        btnLogin.setOnClickListener {
            loginUser()
        }

        // OTP Login
        tvLoginWithOtp.setOnClickListener {
            loginWithOtp()
        }

        // Forgot Password
        tvForgotPassword.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
        }

        // Signup
        tvSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SignupActivity::class.java
                )
            )
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
        }
    }


    // ====================================================
    // NORMAL LOGIN
    // ====================================================

    private fun loginUser() {

        val loginType =
            actLoginType.text.toString().trim()

        val userId =
            etUserId.text.toString().trim()

        val password =
            etPassword.text.toString()

        // ------------------------------------------------
        // Validation
        // ------------------------------------------------

        if (loginType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select login type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (userId.isEmpty()) {

            etUserId.error = "Please enter User ID"

            etUserId.requestFocus()

            return
        }

        if (password.isEmpty()) {

            etPassword.error = "Please enter password"

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
         * IMPORTANT
         *
         * Firestore rules require authentication.
         *
         * Therefore we temporarily authenticate anonymously
         * if there is no authenticated Firebase user.
         */

        val currentUser = auth.currentUser

        // ------------------------------------------------
        // Already authenticated user
        // ------------------------------------------------

        if (currentUser != null && !currentUser.isAnonymous) {

            findUserAndAuthenticate(
                loginType,
                userId,
                password
            )

            return
        }

        // ------------------------------------------------
        // Anonymous / no Firebase session
        // ------------------------------------------------

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
                    "Unable to access account database",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ====================================================
    // FIND USER
    // ====================================================

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

                // ------------------------------------------------
                // User not found
                // ------------------------------------------------

                if (documents.isEmpty) {

                    resetLoginState()

                    Toast.makeText(
                        this,
                        "User ID not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val document = documents.documents[0]

                val email =
                    document.getString("email")?.trim()

                val storedRole =
                    document.getString("role")?.trim()

                // ------------------------------------------------
                // Email check
                // ------------------------------------------------

                if (email.isNullOrBlank()) {

                    resetLoginState()

                    Toast.makeText(
                        this,
                        "Email not found for this account",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Role check
                // ------------------------------------------------

                if (storedRole.isNullOrBlank()) {

                    resetLoginState()

                    Toast.makeText(
                        this,
                        "User role not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Verify selected role
                // ------------------------------------------------

                if (
                    !storedRole.equals(
                        loginType,
                        ignoreCase = true
                    )
                ) {

                    resetLoginState()

                    Toast.makeText(
                        this,
                        "Incorrect account type selected",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                /*
                 * Firestore lookup complete.
                 *
                 * Sign out anonymous user before real
                 * Firebase Authentication.
                 */

                if (auth.currentUser?.isAnonymous == true) {
                    auth.signOut()
                }

                // ------------------------------------------------
                // Firebase Email/Password Authentication
                // ------------------------------------------------

                auth.signInWithEmailAndPassword(
                    email,
                    password
                )

                    .addOnSuccessListener {

                        val firebaseUser =
                            auth.currentUser

                        if (firebaseUser == null) {

                            resetLoginState()

                            Toast.makeText(
                                this,
                                "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@addOnSuccessListener
                        }

                        // ------------------------------------------------
                        // Verify final account
                        // ------------------------------------------------

                        verifyAuthenticatedUser(
                            firebaseUser,
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

                resetLoginState()

                Toast.makeText(
                    this,
                    "Database error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ====================================================
    // VERIFY AUTHENTICATED USER
    // ====================================================

    private fun verifyAuthenticatedUser(
        firebaseUser: FirebaseUser,
        loginType: String,
        userId: String
    ) {

        /*
         * We again find the profile using User ID.
         *
         * This is important because your Firestore document
         * may NOT have the Firebase UID as document ID.
         */

        firestore.collection("users")
            .whereEqualTo("studentId", userId)
            .limit(1)
            .get()

            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    logoutAndReset(
                        "User profile not found"
                    )

                    return@addOnSuccessListener
                }

                val document =
                    documents.documents[0]

                val role =
                    document.getString("role")?.trim()

                val profileEmail =
                    document.getString("email")?.trim()

                val authenticatedEmail =
                    firebaseUser.email?.trim()

                // ------------------------------------------------
                // Role verification
                // ------------------------------------------------

                if (role.isNullOrBlank()) {

                    logoutAndReset(
                        "User role not found"
                    )

                    return@addOnSuccessListener
                }

                if (
                    !role.equals(
                        loginType,
                        ignoreCase = true
                    )
                ) {

                    logoutAndReset(
                        "Account role verification failed"
                    )

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Email verification
                // ------------------------------------------------

                if (
                    profileEmail.isNullOrBlank() ||
                    authenticatedEmail.isNullOrBlank()
                ) {

                    logoutAndReset(
                        "Account email verification failed"
                    )

                    return@addOnSuccessListener
                }

                if (
                    !profileEmail.equals(
                        authenticatedEmail,
                        ignoreCase = true
                    )
                ) {

                    logoutAndReset(
                        "Account profile does not match authentication"
                    )

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Optional UID verification
                // ------------------------------------------------

                val storedUid =
                    document.getString("uid")

                if (
                    !storedUid.isNullOrBlank() &&
                    storedUid != firebaseUser.uid
                ) {

                    logoutAndReset(
                        "Account security verification failed"
                    )

                    return@addOnSuccessListener
                }

                // ------------------------------------------------
                // Everything is correct
                // ------------------------------------------------

                val fullName =
                    document.getString("fullName")
                        ?: "User"

                openCorrectHome(
                    role,
                    userId,
                    fullName
                )
            }

            .addOnFailureListener { exception ->

                logoutAndReset(
                    "Unable to verify account: ${exception.message}"
                )
            }
    }


    // ====================================================
    // OPEN CORRECT HOME
    // ====================================================

    private fun openCorrectHome(
        loginType: String,
        userId: String,
        userName: String
    ) {

        val intent = when {

            loginType.equals(
                "Student",
                ignoreCase = true
            ) -> {

                Intent(
                    this,
                    StudentParentHomeActivity::class.java
                )
            }

            loginType.equals(
                "Parent",
                ignoreCase = true
            ) -> {

                Intent(
                    this,
                    StudentParentHomeActivity::class.java
                )
            }

            loginType.equals(
                "Staff",
                ignoreCase = true
            ) -> {

                Intent(
                    this,
                    StaffHomeActivity::class.java
                )
            }

            else -> {

                Toast.makeText(
                    this,
                    "Invalid account type",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
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


    // ====================================================
    // SESSION MANAGEMENT
    // ====================================================

    private fun checkUserSession() {

        if (sessionCheckCompleted) {
            return
        }

        val currentUser =
            auth.currentUser

        // ------------------------------------------------
        // No session
        // ------------------------------------------------

        if (currentUser == null) {

            sessionCheckCompleted = true

            return
        }

        // ------------------------------------------------
        // VERY IMPORTANT
        //
        // Anonymous user is NOT a real logged-in user.
        //
        // Do not try:
        // users/{anonymousUid}
        // ------------------------------------------------

        if (currentUser.isAnonymous) {

            sessionCheckCompleted = true

            return
        }

        // ------------------------------------------------
        // Real Firebase user
        // ------------------------------------------------

        restoreUserSession(
            currentUser
        )
    }


    // ====================================================
    // RESTORE USER SESSION
    // ====================================================

    private fun restoreUserSession(
        firebaseUser: FirebaseUser
    ) {

        val uid =
            firebaseUser.uid

        /*
         * First try users/{uid}.
         *
         * This supports the recommended Firestore structure.
         */

        firestore.collection("users")
            .document(uid)
            .get()

            .addOnSuccessListener { document ->

                if (document.exists()) {

                    processSessionDocument(
                        document,
                        firebaseUser
                    )

                } else {

                    /*
                     * If document ID is not UID,
                     * find profile by authenticated email.
                     */

                    findSessionByEmail(
                        firebaseUser
                    )
                }
            }

            .addOnFailureListener {

                /*
                 * If direct UID lookup fails,
                 * try email lookup.
                 */

                findSessionByEmail(
                    firebaseUser
                )
            }
    }


    // ====================================================
    // SESSION FALLBACK BY EMAIL
    // ====================================================

    private fun findSessionByEmail(
        firebaseUser: FirebaseUser
    ) {

        val email =
            firebaseUser.email

        if (email.isNullOrBlank()) {

            logoutAndReset(
                "Authenticated account has no email"
            )

            return
        }

        firestore.collection("users")
            .whereEqualTo(
                "email",
                email
            )
            .limit(1)
            .get()

            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    logoutAndReset(
                        "User profile not found"
                    )

                    return@addOnSuccessListener
                }

                processSessionDocument(
                    documents.documents[0],
                    firebaseUser
                )
            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Unable to restore session: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ====================================================
    // PROCESS SESSION DOCUMENT
    // ====================================================

    private fun processSessionDocument(
        document: com.google.firebase.firestore.DocumentSnapshot,
        firebaseUser: FirebaseUser
    ) {

        val role =
            document.getString("role")

        val userId =
            document.getString("studentId")
                ?: ""

        val userName =
            document.getString("fullName")
                ?: "User"

        if (role.isNullOrBlank()) {

            logoutAndReset(
                "User role not found"
            )

            return
        }

        // ------------------------------------------------
        // Verify Email
        // ------------------------------------------------

        val profileEmail =
            document.getString("email")

        val authEmail =
            firebaseUser.email

        if (
            !profileEmail.isNullOrBlank() &&
            !authEmail.isNullOrBlank() &&
            !profileEmail.equals(
                authEmail,
                ignoreCase = true
            )
        ) {

            logoutAndReset(
                "Account profile verification failed"
            )

            return
        }

        // ------------------------------------------------
        // Optional UID verification
        // ------------------------------------------------

        val storedUid =
            document.getString("uid")

        if (
            !storedUid.isNullOrBlank() &&
            storedUid != firebaseUser.uid
        ) {

            logoutAndReset(
                "Account security verification failed"
            )

            return
        }

        // ------------------------------------------------
        // Session valid
        // ------------------------------------------------

        sessionCheckCompleted = true

        openCorrectHome(
            role,
            userId,
            userName
        )
    }


    // ====================================================
    // OTP LOGIN
    // ====================================================

    private fun loginWithOtp() {

        val loginType =
            actLoginType.text.toString().trim()

        val userId =
            etUserId.text.toString().trim()

        if (loginType.isEmpty()) {

            Toast.makeText(
                this,
                "Please select login type",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (userId.isEmpty()) {

            etUserId.error =
                "Please enter User ID"

            etUserId.requestFocus()

            return
        }

        val intent =
            Intent(
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


    // ====================================================
    // LOGIN ERROR MESSAGE
    // ====================================================

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
                "invalid credential",
                ignoreCase = true
            ) == true -> {

                "Incorrect User ID or password"
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


    // ====================================================
    // RESET LOGIN STATE
    // ====================================================

    private fun resetLoginState() {

        auth.signOut()

        btnLogin.isEnabled = true
    }


    // ====================================================
    // LOGOUT + RESET
    // ====================================================

    private fun logoutAndReset(
        message: String
    ) {

        auth.signOut()

        btnLogin.isEnabled = true

        sessionCheckCompleted = true

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }


    // ====================================================
    // OPEN WEBSITE
    // ====================================================

    private fun openWebsite(
        url: String
    ) {

        try {

            val intent =
                Intent(
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