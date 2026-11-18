package com.example.studysync.ui.profile

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout

object LinearLayoutHelper {

    data class ProfileLayout(
        val root: LinearLayout,
        val nameInput: EditText,
        val emailInput: EditText
    )

    fun createProfileLayout(
        context: Context,
        name: String,
        email: String
    ): ProfileLayout {

        val root =
            LinearLayout(context)

        root.orientation =
            LinearLayout.VERTICAL

        root.setPadding(
            40,
            10,
            40,
            0
        )

        val nameInput =
            EditText(context)

        nameInput.hint =
            "Full Name"

        nameInput.setText(
            name
        )

        nameInput.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS

        root.addView(
            nameInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val emailInput =
            EditText(context)

        emailInput.hint =
            "Email"

        emailInput.setText(
            email
        )

        emailInput.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        val emailParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        emailParams.topMargin =
            12

        root.addView(
            emailInput,
            emailParams
        )

        return ProfileLayout(
            root,
            nameInput,
            emailInput
        )
    }
}