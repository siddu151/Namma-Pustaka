package com.example.nammapustaka.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.nammapustaka.database.entity.UserEntity
import com.example.nammapustaka.models.BorrowListRow
import com.example.nammapustaka.models.LeaderboardUiRow
import com.example.nammapustaka.models.OverdueRow
import com.example.nammapustaka.models.RecentTransactionRow
import com.example.nammapustaka.models.ReservationListRow
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.repository.LibraryRepository
import com.example.nammapustaka.utils.SessionManager

class ProfileViewModel(
    private val repository: LibraryRepository,
    private val session: SessionManager
) : ViewModel() {

    private val userId = MutableLiveData(session.userId)

    val me: LiveData<UserEntity?> = userId.switchMap { id ->
        liveData { emit(repository.getUser(id)) }
    }

    val myBorrows: LiveData<List<BorrowListRow>> = userId.switchMap { id ->
        repository.observeMyBorrows(id)
    }

    val leaderboard: LiveData<List<LeaderboardUiRow>> =
        repository.observeLeaderboard(50)

    fun refreshSessionUser() {
        userId.value = session.userId
    }

    fun isAdmin(): Boolean = session.role == UserRole.ADMIN

    suspend fun adminStats(): Triple<Int, Int, Int> = repository.adminCounts()
}

class TransactionsViewModel(
    repository: LibraryRepository
) : ViewModel() {
    val rows: LiveData<List<RecentTransactionRow>> = repository.observeTransactions()
}

class OverdueViewModel(
    repository: LibraryRepository
) : ViewModel() {
    val rows: LiveData<List<OverdueRow>> = repository.observeOverdue()
}

class ReportsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val refresh = MutableLiveData(0)

    val stats: LiveData<Triple<Int, Int, Int>> = refresh.switchMap {
        liveData { emit(repository.adminCounts()) }
    }

    fun reload() {
        refresh.value = (refresh.value ?: 0) + 1
    }
}

class LeaderboardViewModel(
    repository: LibraryRepository
) : ViewModel() {
    val rows: LiveData<List<LeaderboardUiRow>> = repository.observeLeaderboard(50)
}

class ReservedViewModel(
    private val repository: LibraryRepository,
    private val session: SessionManager
) : ViewModel() {
    val rows: LiveData<List<ReservationListRow>> =
        repository.observeMyReservations(session.userId)
}
