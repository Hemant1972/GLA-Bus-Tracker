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
    private lateinit var etPassword: EditText

    private lateinit var btnLogin: Button
    private lateinit var tvLoginWithOtp: TextView
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

        // ------------------------------------------------
        // NORMAL LOGIN
        // ------------------------------------------------

        btnLogin.setOnClickListener {

            loginUser()
        }

        // ------------------------------------------------
        // LOGIN WITH OTP
        // ------------------------------------------------

        tvLoginWithOtp.setOnClickListener {

            loginWithOtp()
        }

        // ------------------------------------------------
        // FORGOT PASSWORD
        // ------------------------------------------------

        tvForgotPassword.setOnClickListener {

            val intent = Intent(
                this,
                ForgotPasswordActivity::class.java
            )

            startActivity(intent)
        }

        // ------------------------------------------------
        // SIGN UP
        // ------------------------------------------------

        tvSignUp.setOnClickListener {

            val intent = Intent(
                this,
                SignupActivity::class.java
            )

            startActivity(intent)
        }

        // ------------------------------------------------
        // ABOUT GLA
        // ------------------------------------------------

        layoutAboutGla.setOnClickListener {

            openWebsite(
                "https://www.gla.ac.in/"
            )
        }

        // ------------------------------------------------
        // FACEBOOK
        // ------------------------------------------------

        layoutFacebook.setOnClickListener {

            openWebsite(
                "https://www.facebook.com/GLAUniversity/"
            )
        }

        // ------------------------------------------------
        // INSTAGRAM
        // ------------------------------------------------

        layoutInstagram.setOnClickListener {

            openWebsite(
                "https://www.instagram.com/glauniversity/"
            )
        }

        // ------------------------------------------------
        // GLAMS
        // ------------------------------------------------

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
    // Normal Login
    // ----------------------------------------------------

    private fun loginUser() {

        val loginType =
            actLoginType.text.toString().trim()

        val userId =
            etUserId.text.toString().trim()

        val password =
            etPassword.text.toString().trim()


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


        // Check Password

        if (password.isEmpty()) {

            etPassword.error =
                "Please enter password"

            etPassword.requestFocus()

            return
        }


        // ------------------------------------------------
        // TEMPORARY LOGIN
        // ------------------------------------------------
        //
        // Abhi database authentication add nahi kiya hai.
        // Student aur Parent dono same portal use karenge.
        // ------------------------------------------------

        if (loginType == "Student" || loginType == "Parent") {

            val intent = Intent(
                this,
                StudentParentHomeActivity::class.java
            )

            intent.putExtra(
                "userName",
                "Hemant Singh"
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

        } else if (loginType == "Staff") {

            Toast.makeText(
                this,
                "Staff Home Page will be added later",
                Toast.LENGTH_SHORT
            ).show()
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


        /*
         * Later:
         *
         * User ID
         *     ↓
         * Database
         *     ↓
         * Registered Mobile Number
         *     ↓
         * Firebase OTP
         */


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