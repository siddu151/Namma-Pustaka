package com.example.nammapustaka.models

/**
 * User roles for rural school library workflows.
 * Stored as plain strings in Room for simplicity.
 */
enum class UserRole {
    STUDENT,
    ADMIN;

    companion object {
        fun fromString(raw: String?): UserRole =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: STUDENT
    }
}
