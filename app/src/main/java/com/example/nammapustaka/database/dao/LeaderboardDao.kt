package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nammapustaka.database.entity.LeaderboardEntity
import com.example.nammapustaka.models.LeaderboardUiRow

@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(row: LeaderboardEntity)

    @Update
    suspend fun update(row: LeaderboardEntity)

    @Query("SELECT * FROM leaderboard WHERE studentId = :studentId LIMIT 1")
    suspend fun getForStudent(studentId: Long): LeaderboardEntity?

    @Query(
        """
        SELECT u.userId AS studentId, u.name, u.imageUri, l.pagesRead, l.booksCompleted
        FROM leaderboard l
        INNER JOIN users u ON u.userId = l.studentId
        ORDER BY l.pagesRead DESC, l.booksCompleted DESC
        LIMIT :limit
        """
    )
    fun observeLeaderboard(limit: Int): LiveData<List<LeaderboardUiRow>>
}
