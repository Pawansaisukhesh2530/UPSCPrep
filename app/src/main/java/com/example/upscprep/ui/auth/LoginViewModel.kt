package com.example.upscprep.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.upscprep.data.model.User
import com.example.upscprep.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for handling authentication logic
 * Manages login and signup operations with state management
 */
class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // LiveData for login state
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    // LiveData for signup state
    private val _signupState = MutableLiveData<SignupState>()
    val signupState: LiveData<SignupState> = _signupState

    /**
     * Sealed class representing login states
     */
    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    /**
     * Sealed class representing signup states
     */
    sealed class SignupState {
        object Loading : SignupState()
        data class Success(val user: User) : SignupState()
        data class Error(val message: String) : SignupState()
    }

    /**
     * Handles user login
     * Validates input and calls repository to authenticate user
     *
     * @param email User's email address
     * @param password User's password
     */
    fun login(email: String, password: String) {
        // Validate input
        if (email.isBlank()) {
            _loginState.value = LoginState.Error("Email cannot be empty")
            return
        }

        if (password.isBlank()) {
            _loginState.value = LoginState.Error("Password cannot be empty")
            return
        }

        // Show loading state
        _loginState.value = LoginState.Loading

        // Perform login in coroutine
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _loginState.value = LoginState.Success(user)
                    } else {
                        _loginState.value = LoginState.Error("Login failed. Please try again.")
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = when {
                        exception?.message?.contains("password") == true -> "Incorrect password"
                        exception?.message?.contains("user") == true -> "User not found"
                        exception?.message?.contains("network") == true -> "Network error. Please check your connection."
                        else -> exception?.message ?: "Login failed. Please try again."
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Handles user signup
     * Validates input and calls repository to create new user account
     *
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param confirmPassword Password confirmation
     */
    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        // Validate input
        if (name.isBlank()) {
            _signupState.value = SignupState.Error("Name cannot be empty")
            return
        }

        if (email.isBlank()) {
            _signupState.value = SignupState.Error("Email cannot be empty")
            return
        }

        if (password.isBlank()) {
            _signupState.value = SignupState.Error("Password cannot be empty")
            return
        }

        if (password.length < 6) {
            _signupState.value = SignupState.Error("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            _signupState.value = SignupState.Error("Passwords do not match")
            return
        }

        // Show loading state
        _signupState.value = SignupState.Loading

        // Perform signup in coroutine
        viewModelScope.launch {
            try {
                val result = authRepository.signUp(name, email, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _signupState.value = SignupState.Success(user)
                    } else {
                        _signupState.value = SignupState.Error("Signup failed. Please try again.")
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = when {
                        exception?.message?.contains("already in use") == true -> "Email already registered"
                        exception?.message?.contains("email") == true -> "Invalid email format"
                        exception?.message?.contains("network") == true -> "Network error. Please check your connection."
                        else -> exception?.message ?: "Signup failed. Please try again."
                    }
                    _signupState.value = SignupState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _signupState.value = SignupState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

