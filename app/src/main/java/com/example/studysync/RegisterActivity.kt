package com.example.studysync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword =
            findViewById(R.id.etConfirmPassword)

        btnRegister =
            findViewById(R.id.btnRegister)

        tvLogin =
            findViewById(R.id.tvLogin)

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {

            finish()
        }
    }

    private fun registerUser() {

        val name =
            etName.text.toString().trim()

        val email =
            etEmail.text.toString().trim()

        val password =
            etPassword.text.toString()

        val confirmPassword =
            etConfirmPassword.text.toString()

        if (name.isEmpty()) {
            etName.error = "Enter your name"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
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

        val preferences =
            getSharedPreferences(
                "StudySyncPrefs",
                MODE_PRIVATE
            )

        preferences.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .putBoolean("isLoggedIn", true)
            .apply()

        Toast.makeText(
            this,
            "Account created successfully! 🎉",
            Toast.LENGTH_SHORT
        ).show()

        openMainActivity()
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