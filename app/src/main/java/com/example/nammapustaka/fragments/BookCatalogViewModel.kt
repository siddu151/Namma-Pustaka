package com.example.nammapustaka.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.repository.LibraryRepository

class BookCatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val filters = MutableLiveData(Pair("", null as String?))

    val books: LiveData<List<BookEntity>> = filters.switchMap { (q, cat) ->
        repository.observeBooks(q, cat)
    }

    init {
        filters.value = Pair("", null)
    }

    fun setQuery(q: String) {
        val cat = filters.value?.second
        filters.value = Pair(q, cat)
    }

    fun setCategory(category: String?) {
        val q = filters.value?.first ?: ""
        filters.value = Pair(q, category)
    }
}
