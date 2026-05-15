package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Issue / return lifecycle for a single copy of a book.
 * [status] is "ISSUED" while borrowed, "RETURNED" after check-in.
 */
@Entity(
    tableName = "transactions",
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
    indices = [Index("bookId"), Index("studentId"), Index("status")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Long = 0,
    val bookId: Long,
    val studentId: Long,
    val issueDate: Date,
    val dueDate: Date,
    val returnDate: Date? = null,
    val status: String
)
