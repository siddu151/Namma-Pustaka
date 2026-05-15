package com.example.nammapustaka.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Aggregated reading stats per student for leaderboard screens.
 */
@Entity(
    tableName = "leaderboard",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"], unique = true)]
)
data class LeaderboardEntity(
    @PrimaryKey val studentId: Long,
    val pagesRead: Int = 0,
    val booksCompleted: Int = 0
)
