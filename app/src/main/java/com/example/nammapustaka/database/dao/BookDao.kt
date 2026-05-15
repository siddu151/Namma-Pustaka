package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nammapustaka.database.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    fun observeAll(): LiveData<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE (title LIKE '%' || :q || '%' OR author LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%')
        AND ( :category = '__ALL__' OR category = :category )
        ORDER BY createdAt DESC
        """
    )
    fun observeSearch(q: String, category: String): LiveData<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :id LIMIT 1")
    suspend fun getById(id: Long): BookEntity?

    @Query("SELECT * FROM books WHERE qrCode = :qr LIMIT 1")
    suspend fun getByQr(qr: String): BookEntity?

    @Query("SELECT * FROM books WHERE LOWER(title) = LOWER(:title) AND LOWER(author) = LOWER(:author) LIMIT 1")
    suspend fun findDuplicate(title: String, author: String): BookEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.ABORT)
    suspend fun insert(book: BookEntity): Long

    @Update
    suspend fun update(book: BookEntity)

    @Query("DELETE FROM books WHERE bookId = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM books")
    fun observeBookCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun countBooks(): Int
}
