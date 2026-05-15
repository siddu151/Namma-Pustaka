package com.example.nammapustaka.fragments.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.repository.LibraryRepository
import com.example.nammapustaka.utils.InputValidator
import com.example.nammapustaka.utils.SessionManager

class AuthViewModel(
    private val repository: LibraryRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    fun login(email: String, password: String) = liveData {
        _loginError.postValue(null)
        when {
            email.isBlank() || password.isBlank() -> {
                _loginError.postValue("Fill all fields")
                emit(null)
            }
            !InputValidator.isValidEmail(email) -> {
                _loginError.postValue("Invalid email")
                emit(null)
            }
            !InputValidator.isValidPassword(password) -> {
                _loginError.postValue("Password must be at least ${InputValidator.MIN_PASSWORD} characters")
                emit(null)
            }
            else -> {
                val r = repository.login(email, password)
                r.onSuccess { user ->
                    session.userId = user.userId
                    session.role = UserRole.fromString(user.role)
                    session.email = user.email
                    session.displayName = user.name
                    emit(user)
                }.onFailure { e ->
                    _loginError.postValue(e.message ?: "Login failed")
                    emit(null)
                }
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        className: String?
    ) = liveData {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                emit(Result.failure(IllegalArgumentException("Fill all fields")))
            !InputValidator.isValidEmail(email) ->
                emit(Result.failure(IllegalArgumentException("Invalid email")))
            !InputValidator.isValidPassword(password) ->
                emit(Result.failure(IllegalArgumentException("Password too short")))
            role == UserRole.STUDENT && className.isNullOrBlank() ->
                emit(Result.failure(IllegalArgumentException("Enter class for student")))
            else -> {
                val r = repository.register(name, email, password, role, className)
                emit(r)
            }
        }
    }
}
