package com.example.nammapustaka.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.repository.LibraryRepository
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditBookViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    suspend fun load(bookId: Long): BookEntity? =
        if (bookId <= 0) null else repository.getBook(bookId)

    fun save(
        bookId: Long,
        title: String,
        author: String,
        category: String,
        description: String,
        pages: Int,
        imageUri: String?,
        qrCode: String,
        onDone: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val r: Result<Unit> = if (bookId <= 0) {
                repository.addBook(title, author, category, description, imageUri, pages, qrCode)
                    .fold(
                        onSuccess = { Result.success(Unit) },
                        onFailure = { Result.failure(it) }
                    )
            } else {
                val existing = repository.getBook(bookId)
                if (existing == null) {
                    Result.failure(IllegalStateException("Book not found"))
                } else {
                    repository.updateBook(
                        existing.copy(
                            title = title.trim(),
                            author = author.trim(),
                            category = category.trim(),
                            description = description.trim(),
                            imageUri = imageUri,
                            totalPages = pages,
                            qrCode = qrCode.trim()
                        )
                    )
                }
            }
            onDone(r)
        }
    }

    fun newQrPayload(): String = "NP-${UUID.randomUUID()}"
}
