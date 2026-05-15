package com.example.nammapustaka.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.repository.LibraryRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class IssueReturnViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    suspend fun findBook(qr: String): BookEntity? = repository.getBookByQr(qr)

    suspend fun activeTransaction(bookId: Long) = repository.getActiveTransaction(bookId)

    fun issue(bookId: Long, studentId: Long, dueDate: Date, onDone: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onDone(repository.issueBook(bookId, studentId, dueDate))
        }
    }

    fun returnBook(bookId: Long, onDone: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onDone(repository.returnBook(bookId))
        }
    }

    fun defaultDueDate(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 14)
        return cal.time
    }
}
