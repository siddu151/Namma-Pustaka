package com.example.nammapustaka.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.models.ReviewListRow
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.repository.LibraryRepository
import com.example.nammapustaka.utils.SessionManager

class BookDetailViewModel(
    private val repository: LibraryRepository,
    private val session: SessionManager
) : ViewModel() {

    private val bookId = MutableLiveData<Long>()

    val book: LiveData<BookEntity?> = bookId.switchMap { id ->
        liveData { emit(repository.getBook(id)) }
    }

    val reviews: LiveData<List<ReviewListRow>> = bookId.switchMap { id ->
        repository.observeReviews(id)
    }

    val averageRating: LiveData<Float?> = bookId.switchMap { id ->
        repository.observeAverageRating(id)
    }

    fun setBookId(id: Long) {
        if (bookId.value != id) bookId.value = id
    }

    /** Re-query book row (e.g. after AI summary cached). */
    fun refreshBook() {
        val id = bookId.value ?: return
        bookId.value = -1L
        bookId.value = id
    }

    suspend fun deleteBook(): Result<Unit> {
        val bid = bookId.value ?: return Result.failure(IllegalStateException("No book"))
        return repository.deleteBook(bid)
    }

    fun currentUserId(): Long = session.userId

    fun isAdmin(): Boolean = session.role == UserRole.ADMIN

    suspend fun reserve(): Result<Unit> {
        val bid = bookId.value ?: return Result.failure(IllegalStateException("No book"))
        return repository.reserveBook(bid, session.userId)
    }

    suspend fun addReview(rating: Float, text: String): Result<Unit> {
        val bid = bookId.value ?: return Result.failure(IllegalStateException("No book"))
        return repository.addReview(bid, session.userId, rating, text)
    }

    suspend fun generateSummary(): Result<Pair<String, String>> {
        val bid = bookId.value ?: return Result.failure(IllegalStateException("No book"))
        return repository.generateKannadaSummary(bid)
    }
}
