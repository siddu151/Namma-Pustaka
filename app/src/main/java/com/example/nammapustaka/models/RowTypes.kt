package com.example.nammapustaka.models

import androidx.room.ColumnInfo
import java.util.Date

/** Flat row for active borrow lists (Room maps column aliases to fields). */
data class BorrowListRow(
    val transactionId: Long,
    val bookId: Long,
    val studentId: Long,
    val issueDate: Date,
    val dueDate: Date,
    val returnDate: Date?,
    val status: String,
    @ColumnInfo(name = "bookTitle") val bookTitle: String,
    @ColumnInfo(name = "bookAuthor") val bookAuthor: String,
    @ColumnInfo(name = "studentName") val studentName: String
)

data class OverdueRow(
    val transactionId: Long,
    val bookId: Long,
    val studentId: Long,
    val issueDate: Date,
    val dueDate: Date,
    val returnDate: Date?,
    val status: String,
    @ColumnInfo(name = "bookTitle") val bookTitle: String,
    @ColumnInfo(name = "bookAuthor") val bookAuthor: String,
    @ColumnInfo(name = "studentName") val studentName: String,
    @ColumnInfo(name = "overdueDays") val overdueDays: Long
)

data class LeaderboardUiRow(
    val studentId: Long,
    val name: String,
    val imageUri: String?,
    val pagesRead: Int,
    val booksCompleted: Int
)

data class ReviewListRow(
    val reviewId: Long,
    val bookId: Long,
    val studentId: Long,
    val rating: Float,
    val reviewText: String,
    @ColumnInfo(name = "studentName") val studentName: String
)

data class RecentTransactionRow(
    val transactionId: Long,
    val bookId: Long,
    val studentId: Long,
    val issueDate: Date,
    val dueDate: Date,
    val returnDate: Date?,
    val status: String,
    @ColumnInfo(name = "bookTitle") val bookTitle: String,
    @ColumnInfo(name = "studentName") val studentName: String
)

data class ReservationListRow(
    val reservationId: Long,
    val bookId: Long,
    val studentId: Long,
    val createdAt: Date,
    val active: Boolean,
    @ColumnInfo(name = "bookTitle") val bookTitle: String
)
