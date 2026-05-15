package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * When a book is unavailable, students can queue a reservation.
 * Admin / system can notify when the title is returned.
 */
@Entity(
    tableName = "reservations",
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
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val reservationId: Long = 0,
    val bookId: Long,
    val studentId: Long,
    val createdAt: Date = Date(),
    /** Active until fulfilled or cancelled */
    val active: Boolean = true
)
