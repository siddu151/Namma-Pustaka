package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nammapustaka.database.entity.ReviewEntity
import com.example.nammapustaka.models.ReviewListRow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: ReviewEntity): Long

    @Query(
        """
        SELECT r.reviewId, r.bookId, r.studentId, r.rating, r.reviewText, u.name AS studentName
        FROM reviews r
        INNER JOIN users u ON u.userId = r.studentId
        WHERE r.bookId = :bookId
        ORDER BY r.createdAt DESC
        """
    )
    fun observeForBook(bookId: Long): LiveData<List<ReviewListRow>>

    @Query("SELECT AVG(rating) FROM reviews WHERE bookId = :bookId")
    fun observeAverageRating(bookId: Long): LiveData<Float?>
}
