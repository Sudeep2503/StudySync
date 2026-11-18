package com.example.studysync.data

import android.content.Context

class AuthManager(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "StudySyncAuth",
            Context.MODE_PRIVATE
        )

    fun register(
        name: String,
        email: String,
        password: String
    ) {

        preferences.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .putBoolean("loggedIn", true)
            .apply()
    }

    fun login(
        email: String,
        password: String
    ): Boolean {

        val savedEmail =
            preferences.getString("email", null)

        val savedPassword =
            preferences.getString("password", null)

        if (
            email == savedEmail &&
            password == savedPassword
        ) {

            preferences.edit()
                .putBoolean("loggedIn", true)
                .apply()

            return true
        }

        return false
    }

    fun logout() {

        preferences.edit()
            .putBoolean("loggedIn", false)
            .apply()
    }

    fun isLoggedIn(): Boolean {

        return preferences.getBoolean(
            "loggedIn",
            false
        )
    }

    fun getName(): String {

        return preferences.getString(
            "name",
            "Student"
        ) ?: "Student"
    }

    fun getEmail(): String {

        return preferences.getString(
            "email",
            ""
        ) ?: ""
    }
}