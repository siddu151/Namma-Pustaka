package com.example.nammapustaka.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.nammapustaka.models.UserRole

/**
 * Persists signed-in user session (demo-friendly SharedPreferences).
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var userId: Long
        get() = prefs.getLong(KEY_USER_ID, -1L)
        set(value) = prefs.edit().putLong(KEY_USER_ID, value).apply()

    var role: UserRole
        get() = UserRole.fromString(prefs.getString(KEY_ROLE, null))
        set(value) = prefs.edit().putString(KEY_ROLE, value.name).apply()

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var displayName: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    val isLoggedIn: Boolean get() = userId > 0

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS = "nammapustaka_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ROLE = "role"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
    }
}
