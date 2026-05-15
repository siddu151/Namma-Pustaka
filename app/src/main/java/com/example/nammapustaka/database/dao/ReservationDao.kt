package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nammapustaka.database.entity.ReservationEntity
import com.example.nammapustaka.models.ReservationListRow

@Dao
interface ReservationDao {
    @Insert
    suspend fun insert(r: ReservationEntity): Long

    @Update
    suspend fun update(r: ReservationEntity)

    @Query(
        """
        SELECT r.reservationId, r.bookId, r.studentId, r.createdAt, r.active, b.title AS bookTitle
        FROM reservations r
        INNER JOIN books b ON b.bookId = r.bookId
        WHERE r.studentId = :studentId AND r.active = 1
        ORDER BY r.createdAt DESC
        """
    )
    fun observeForStudent(studentId: Long): LiveData<List<ReservationListRow>>

    @Query("SELECT * FROM reservations WHERE bookId = :bookId AND active = 1 ORDER BY createdAt ASC")
    suspend fun pendingForBook(bookId: Long): List<ReservationEntity>

    @Query("SELECT COUNT(*) FROM reservations WHERE studentId = :studentId AND bookId = :bookId AND active = 1")
    suspend fun countActive(studentId: Long, bookId: Long): Int
}
