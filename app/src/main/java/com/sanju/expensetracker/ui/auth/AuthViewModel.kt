package com.sanju.expensetracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanju.expensetracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun loginUser(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading

            val result = repository.loginUser(email, password)

            _authState.value = if (result.isSuccess) {

                AuthState.Success(
                    result.getOrNull() ?: "Success"
                )

            } else {

                AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }

    fun registerUser(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _authState.value = AuthState.Loading

            val result = repository.registerUser(email, password)

            _authState.value = if (result.isSuccess) {

                AuthState.Success(
                    result.getOrNull() ?: "Success"
                )

            } else {

                AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }
}

sealed class AuthState {

    data object Idle : AuthState()

    data object Loading : AuthState()

    data class Success(
        val message: String
    ) : AuthState()

    data class Error(
        val message: String
    ) : AuthState()
}