package com.example.studysync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If user is already logged in, go directly to MainActivity
        val preferences = getSharedPreferences(
            "StudySyncPrefs",
            MODE_PRIVATE
        )

        val isLoggedIn = preferences.getBoolean(
            "isLoggedIn",
            false
        )

        if (isLoggedIn) {
            openMainActivity()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(
                this,
                RegisterActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter your password"
            etPassword.requestFocus()
            return
        }

        val preferences = getSharedPreferences(
            "StudySyncPrefs",
            MODE_PRIVATE
        )

        val savedEmail = preferences.getString(
            "email",
            ""
        )

        val savedPassword = preferences.getString(
            "password",
            ""
        )

        if (
            email == savedEmail &&
            password == savedPassword
        ) {

            preferences.edit()
                .putBoolean(
                    "isLoggedIn",
                    true
                )
                .apply()

            Toast.makeText(
                this,
                "Login successful! 🎉",
                Toast.LENGTH_SHORT
            ).show()

            openMainActivity()

        } else {

            Toast.makeText(
                this,
                "Invalid email or password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openMainActivity() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}