package com.example.glabustracker

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {

    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText

    private lateinit var tvTimer: TextView
    private lateinit var tvOtpStatus: TextView
    private lateinit var tvResendOtp: TextView

    private lateinit var btnVerifyOtp: Button

    private var countDownTimer: CountDownTimer? = null

    companion object {
        private const val OTP_TIME = 120000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp)

        initializeViews()
        setupOtpMovement()
        startOtpTimer()
        setupButtons()
    }

    private fun initializeViews() {

        etOtp1 = findViewById(R.id.etOtp1)
        etOtp2 = findViewById(R.id.etOtp2)
        etOtp3 = findViewById(R.id.etOtp3)
        etOtp4 = findViewById(R.id.etOtp4)
        etOtp5 = findViewById(R.id.etOtp5)
        etOtp6 = findViewById(R.id.etOtp6)

        tvTimer = findViewById(R.id.tvTimer)
        tvOtpStatus = findViewById(R.id.tvOtpStatus)
        tvResendOtp = findViewById(R.id.tvResendOtp)

        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
    }

    private fun setupOtpMovement() {

        etOtp1.setOnKeyListener { _, keyCode, event ->

            if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                event.action == android.view.KeyEvent.ACTION_DOWN &&
                etOtp1.text.isEmpty()
            ) {
                true
            } else {
                false
            }
        }

        etOtp1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp1.selectAll()
            }
        }

        etOtp2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp2.selectAll()
            }
        }

        etOtp3.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp3.selectAll()
            }
        }

        etOtp4.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp4.selectAll()
            }
        }

        etOtp5.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp5.selectAll()
            }
        }

        etOtp6.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etOtp6.selectAll()
            }
        }

        etOtp1.setOnKeyListener(createPreviousFocusListener(etOtp1, null))
        etOtp2.setOnKeyListener(createPreviousFocusListener(etOtp2, etOtp1))
        etOtp3.setOnKeyListener(createPreviousFocusListener(etOtp3, etOtp2))
        etOtp4.setOnKeyListener(createPreviousFocusListener(etOtp4, etOtp3))
        etOtp5.setOnKeyListener(createPreviousFocusListener(etOtp5, etOtp4))
        etOtp6.setOnKeyListener(createPreviousFocusListener(etOtp6, etOtp5))

        etOtp1.setOnEditorActionListener { _, _, _ ->
            false
        }
    }

    private fun createPreviousFocusListener(
        current: EditText,
        previous: EditText?
    ): android.view.View.OnKeyListener {

        return android.view.View.OnKeyListener { _, keyCode, event ->

            if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                event.action == android.view.KeyEvent.ACTION_DOWN &&
                current.text.isEmpty()
            ) {

                previous?.requestFocus()

                true

            } else {

                false
            }
        }
    }

    private fun startOtpTimer() {

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(
            OTP_TIME,
            1000
        ) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds =
                    millisUntilFinished / 1000

                val minutes =
                    seconds / 60

                val remainingSeconds =
                    seconds % 60

                tvTimer.text = String.format(
                    "%02d:%02d",
                    minutes,
                    remainingSeconds
                )
            }

            override fun onFinish() {

                tvTimer.text = "00:00"

                tvOtpStatus.text =
                    "OTP expired. Please request a new OTP."

                btnVerifyOtp.isEnabled = false
                tvResendOtp.isEnabled = true
            }

        }.start()
    }

    private fun setupButtons() {

        btnVerifyOtp.setOnClickListener {

            verifyOtp()
        }

        tvResendOtp.setOnClickListener {

            resendOtp()
        }
    }

    private fun getEnteredOtp(): String {

        return etOtp1.text.toString() +
                etOtp2.text.toString() +
                etOtp3.text.toString() +
                etOtp4.text.toString() +
                etOtp5.text.toString() +
                etOtp6.text.toString()
    }

    private fun verifyOtp() {

        val otp = getEnteredOtp()

        if (otp.length != 6) {

            Toast.makeText(
                this,
                "Please enter 6 digit OTP",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        /*
         * Firebase OTP verification
         * will be added here.
         */

        Toast.makeText(
            this,
            "OTP verified successfully",
            Toast.LENGTH_SHORT
        ).show()

        // Temporary navigation
        val intent = Intent(
            this,
            MainActivity::class.java
        )

        startActivity(intent)
        finish()
    }

    private fun resendOtp() {

        clearOtpFields()

        btnVerifyOtp.isEnabled = true

        tvOtpStatus.text =
            "A new OTP has been sent. OTP is valid for 2 minutes."

        Toast.makeText(
            this,
            "OTP resent successfully",
            Toast.LENGTH_SHORT
        ).show()

        startOtpTimer()
    }

    private fun clearOtpFields() {

        etOtp1.text.clear()
        etOtp2.text.clear()
        etOtp3.text.clear()
        etOtp4.text.clear()
        etOtp5.text.clear()
        etOtp6.text.clear()

        etOtp1.requestFocus()
    }

    override fun onDestroy() {

        countDownTimer?.cancel()

        super.onDestroy()
    }
}