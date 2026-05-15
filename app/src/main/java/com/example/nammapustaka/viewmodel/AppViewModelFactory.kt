package com.example.nammapustaka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.fragments.auth.AuthViewModel
import com.example.nammapustaka.fragments.AddEditBookViewModel
import com.example.nammapustaka.fragments.BookCatalogViewModel
import com.example.nammapustaka.fragments.BookDetailViewModel
import com.example.nammapustaka.fragments.IssueReturnViewModel
import com.example.nammapustaka.fragments.LeaderboardViewModel
import com.example.nammapustaka.fragments.OverdueViewModel
import com.example.nammapustaka.fragments.ProfileViewModel
import com.example.nammapustaka.fragments.ReportsViewModel
import com.example.nammapustaka.fragments.ReservedViewModel
import com.example.nammapustaka.fragments.TransactionsViewModel

/**
 * Central [ViewModelProvider.Factory] wiring app-wide dependencies.
 */
class AppViewModelFactory(
    private val app: NammaPustakaApp
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = app.repository
        val session = app.sessionManager
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repo, session) as T
            modelClass.isAssignableFrom(BookCatalogViewModel::class.java) ->
                BookCatalogViewModel(repo) as T
            modelClass.isAssignableFrom(BookDetailViewModel::class.java) ->
                BookDetailViewModel(repo, session) as T
            modelClass.isAssignableFrom(AddEditBookViewModel::class.java) ->
                AddEditBookViewModel(repo) as T
            modelClass.isAssignableFrom(IssueReturnViewModel::class.java) ->
                IssueReturnViewModel(repo) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repo, session) as T
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) ->
                TransactionsViewModel(repo) as T
            modelClass.isAssignableFrom(OverdueViewModel::class.java) ->
                OverdueViewModel(repo) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) ->
                ReportsViewModel(repo) as T
            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) ->
                LeaderboardViewModel(repo) as T
            modelClass.isAssignableFrom(ReservedViewModel::class.java) ->
                ReservedViewModel(repo, session) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }

    companion object {
        fun from(app: NammaPustakaApp) = AppViewModelFactory(app)
    }
}
