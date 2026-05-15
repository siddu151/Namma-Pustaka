package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nammapustaka.database.entity.TransactionEntity
import com.example.nammapustaka.models.BorrowListRow
import com.example.nammapustaka.models.OverdueRow
import com.example.nammapustaka.models.RecentTransactionRow
import java.util.Date

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(tx: TransactionEntity): Long

    @Query("UPDATE transactions SET returnDate = :returnedAt, status = 'RETURNED' WHERE transactionId = :txId")
    suspend fun markReturned(txId: Long, returnedAt: Date)

    @Query(
        """
        SELECT t.transactionId, t.bookId, t.studentId, t.issueDate, t.dueDate, t.returnDate, t.status,
               b.title AS bookTitle, b.author AS bookAuthor,
               u.name AS studentName
        FROM transactions t
        INNER JOIN books b ON b.bookId = t.bookId
        INNER JOIN users u ON u.userId = t.studentId
        WHERE t.status = 'ISSUED'
        ORDER BY t.dueDate ASC
        """
    )
    fun observeActiveBorrowRows(): LiveData<List<BorrowListRow>>

    @Query(
        """
        SELECT t.transactionId, t.bookId, t.studentId, t.issueDate, t.dueDate, t.returnDate, t.status,
               b.title AS bookTitle, b.author AS bookAuthor,
               u.name AS studentName,
               ((CAST(strftime('%s','now') AS INTEGER) * 1000 - t.dueDate) / 86400000) AS overdueDays
        FROM transactions t
        INNER JOIN books b ON b.bookId = t.bookId
        INNER JOIN users u ON u.userId = t.studentId
        WHERE t.status = 'ISSUED' AND t.dueDate < (CAST(strftime('%s','now') AS INTEGER) * 1000)
        ORDER BY t.dueDate ASC
        """
    )
    fun observeOverdueRows(): LiveData<List<OverdueRow>>

    @Query(
        """
        SELECT t.transactionId, t.bookId, t.studentId, t.issueDate, t.dueDate, t.returnDate, t.status,
               b.title AS bookTitle, b.author AS bookAuthor,
               u.name AS studentName,
               ((CAST(strftime('%s','now') AS INTEGER) * 1000 - t.dueDate) / 86400000) AS overdueDays
        FROM transactions t
        INNER JOIN books b ON b.bookId = t.bookId
        INNER JOIN users u ON u.userId = t.studentId
        WHERE t.status = 'ISSUED' AND t.dueDate < (CAST(strftime('%s','now') AS INTEGER) * 1000)
        ORDER BY t.dueDate ASC
        """
    )
    suspend fun loadOverdueRows(): List<OverdueRow>

    @Query(
        """
        SELECT * FROM transactions
        WHERE bookId = :bookId AND status = 'ISSUED'
        LIMIT 1
        """
    )
    suspend fun getActiveForBook(bookId: Long): TransactionEntity?

    @Query(
        """
        SELECT t.transactionId, t.bookId, t.studentId, t.issueDate, t.dueDate, t.returnDate, t.status,
               b.title AS bookTitle, u.name AS studentName
        FROM transactions t
        INNER JOIN books b ON b.bookId = t.bookId
        INNER JOIN users u ON u.userId = t.studentId
        ORDER BY t.issueDate DESC LIMIT 200
        """
    )
    fun observeRecentRows(): LiveData<List<RecentTransactionRow>>

    @Query("SELECT COUNT(*) FROM transactions")
    fun observeTransactionCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countTransactions(): Int

    @Query(
        """
        SELECT t.transactionId, t.bookId, t.studentId, t.issueDate, t.dueDate, t.returnDate, t.status,
               b.title AS bookTitle, b.author AS bookAuthor,
               u.name AS studentName
        FROM transactions t
        INNER JOIN books b ON b.bookId = t.bookId
        INNER JOIN users u ON u.userId = t.studentId
        WHERE t.studentId = :studentId AND t.status = 'ISSUED'
        ORDER BY t.dueDate ASC
        """
    )
    fun observeForStudent(studentId: Long): LiveData<List<BorrowListRow>>

    @Query("SELECT * FROM transactions WHERE bookId = :bookId ORDER BY issueDate DESC LIMIT 1")
    suspend fun latestForBook(bookId: Long): TransactionEntity?
}
