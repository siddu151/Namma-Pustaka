package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("studentId")]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val reviewId: Long = 0,
    val bookId: Long,
    val studentId: Long,
    val rating: Float,
    val reviewText: String,
    val createdAt: Date = Date()
)
