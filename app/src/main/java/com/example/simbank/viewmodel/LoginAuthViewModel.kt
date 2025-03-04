package com.example.simbank.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simbank.datamodels.UserAccount
import com.example.simbank.repository.UserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

class LoginAuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val TAG = "LoginAuthViewModel"

    // State holder for authentication results
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    // Timeout duration for Firebase operations (in milliseconds)
    private val FIREBASE_TIMEOUT = 10000L // 10 seconds

    fun loginUser(email: String, password: String) {
        // Input validation
        if (email.isBlank() || password.isBlank()) {
            authResultState.value = AuthResult.Error("Email and password are required.")
            return
        }

        // Set loading state
        authResultState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                // Attempt Firebase authentication with timeout
                withTimeout(FIREBASE_TIMEOUT) {
                    val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

                    // Handle successful authentication
                    val user = authResult.user
                    if (user != null) {
                        try {
                            updateUserAccount(user, email)
                            authResultState.value = AuthResult.Success
                        } catch (e: Exception) {
                            // Handle repository operation failure
                            handleError("Failed to update user data: ${e.localizedMessage}")
                            logout() // Cleanup on failure
                        }
                    } else {
                        handleError("User authentication failed")
                    }
                }
            } catch (e: Exception) {
                // Handle specific Firebase exceptions
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                    is FirebaseAuthInvalidUserException -> "Account doesn't exist"
                    is FirebaseException -> "Network error. Please check your connection"
                    is CancellationException -> "Operation timed out"
                    else -> e.localizedMessage ?: "Authentication failed"
                }
                handleError(errorMessage)
            }
        }
    }

    // Helper function to update user account in repository
    private suspend fun updateUserAccount(user: FirebaseUser, email: String) {
        val existingAccount = userRepository.getUserAccountOnce(user.uid)

        val updatedAccount = existingAccount?.copy(
            fullName = user.displayName ?: "",
            email = email
        ) ?: UserAccount(
            uid = user.uid,
            fullName = user.displayName ?: "",
            email = email
        )

        userRepository.createOrUpdateUserAccount(updatedAccount)
    }

    // Centralized error handling
    private fun handleError(message: String) {
        Log.e(TAG, message)
        authResultState.value = AuthResult.Error(message)
    }

    fun logout() {
        try {
            firebaseAuth.signOut()
            authResultState.value = AuthResult.Idle
        } catch (e: Exception) {
            handleError("Logout failed: ${e.localizedMessage}")
        }
    }

    // Enhanced auth state check with token validation
    fun checkAuthState(): Boolean {
        return try {
            val user = firebaseAuth.currentUser
            // Check if user exists and token is valid
            user?.let {
                it.reload().isSuccessful && !it.isAnonymous
            } ?: false
        } catch (e: Exception) {
            handleError("Auth state check failed: ${e.localizedMessage}")
            false
        }
    }
}