package com.example.studysync.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studysync.MainActivity
import com.example.studysync.R
import com.example.studysync.data.AuthManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var authManager: AuthManager

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_login
        )

        authManager =
            AuthManager(this)

        initializeViews()

        setupListeners()
    }

    private fun initializeViews() {

        etEmail =
            findViewById(R.id.etEmail)

        etPassword =
            findViewById(R.id.etPassword)

        btnLogin =
            findViewById(R.id.btnLogin)

        tvRegister =
            findViewById(R.id.tvRegister)
    }

    private fun setupListeners() {

        btnLogin.setOnClickListener {

            loginUser()
        }

        tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun loginUser() {

        val email =
            etEmail.text
                .toString()
                .trim()

        val password =
            etPassword.text
                .toString()

        if (email.isEmpty()) {

            etEmail.error =
                "Enter your email"

            etEmail.requestFocus()

            return
        }

        if (password.isEmpty()) {

            etPassword.error =
                "Enter your password"

            etPassword.requestFocus()

            return
        }

        val success =
            authManager.login(
                email,
                password
            )

        if (success) {

            Toast.makeText(
                this,
                "Login successful! Welcome back 👋",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            startActivity(intent)

            finish()

        } else {

            Toast.makeText(
                this,
                "Invalid email or password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}