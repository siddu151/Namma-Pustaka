package com.example.nammapustaka.utils

import android.util.Patterns

object InputValidator {
    const val MIN_PASSWORD = 6

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD

    fun nonEmpty(vararg fields: String): Boolean = fields.all { it.isNotBlank() }
}
