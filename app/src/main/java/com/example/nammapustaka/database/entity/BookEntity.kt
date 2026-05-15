package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["title"]),
        Index(value = ["author"]),
        Index(value = ["category"]),
        Index(value = ["qrCode"], unique = true)
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val bookId: Long = 0,
    val title: String,
    val author: String,
    val category: String,
    val description: String,
    val imageUri: String? = null,
    /** Unique payload encoded into the book QR sticker */
    val qrCode: String,
    val available: Boolean = true,
    val totalPages: Int = 0,
    val createdAt: Date = Date(),
    /** Cached Gemini Kannada summary (optional) */
    val kannadaSummary: String? = null,
    /** Cached difficulty label e.g. "ಸುಲಭ" */
    val readingDifficulty: String? = null
)
