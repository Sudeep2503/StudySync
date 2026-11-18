package com.example.studysync.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studysync.R
import com.example.studysync.data.AuthManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    private lateinit var authManager: AuthManager

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_register
        )

        authManager =
            AuthManager(this)

        initializeViews()

        setupListeners()
    }

    private fun initializeViews() {

        etName =
            findViewById(R.id.etName)

        etEmail =
            findViewById(R.id.etEmail)

        etPassword =
            findViewById(R.id.etPassword)

        etConfirmPassword =
            findViewById(
                R.id.etConfirmPassword
            )

        btnRegister =
            findViewById(R.id.btnRegister)

        tvLogin =
            findViewById(R.id.tvLogin)
    }

    private fun setupListeners() {

        btnRegister.setOnClickListener {

            registerUser()
        }

        tvLogin.setOnClickListener {

            finish()
        }
    }

    private fun registerUser() {

        val name =
            etName.text
                .toString()
                .trim()

        val email =
            etEmail.text
                .toString()
                .trim()

        val password =
            etPassword.text
                .toString()

        val confirmPassword =
            etConfirmPassword.text
                .toString()

        if (name.isEmpty()) {

            etName.error =
                "Enter your name"

            etName.requestFocus()

            return
        }

        if (email.isEmpty()) {

            etEmail.error =
                "Enter your email"

            etEmail.requestFocus()

            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            etEmail.error =
                "Enter a valid email"

            etEmail.requestFocus()

            return
        }

        if (password.isEmpty()) {

            etPassword.error =
                "Enter a password"

            etPassword.requestFocus()

            return
        }

        if (password.length < 6) {

            etPassword.error =
                "Password must be at least 6 characters"

            etPassword.requestFocus()

            return
        }

        if (confirmPassword != password) {

            etConfirmPassword.error =
                "Passwords do not match"

            etConfirmPassword.requestFocus()

            return
        }

        authManager.register(
            name,
            email,
            password
        )

        Toast.makeText(
            this,
            "Account created successfully! 🎉",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}