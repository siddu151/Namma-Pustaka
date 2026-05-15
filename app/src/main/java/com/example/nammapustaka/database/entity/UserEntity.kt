package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registered library user (student or admin/teacher).
 * Password is stored in plain text for demo / academic use only — hash in production.
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    /** "STUDENT" or "ADMIN" */
    val role: String,
    val className: String? = null,
    val imageUri: String? = null
)
