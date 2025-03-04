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

class RegisterAuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val TAG = "RegisterAuthViewModel"

    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    private val FIREBASE_TIMEOUT = 10000L // 10 seconds

    fun registerUser(fullName: String, email: String, password: String, confirmPassword: String) {
        Log.d(TAG, "Registration attempt for email: $email")

        if (!validateInput(fullName, email, password, confirmPassword)) {
            return
        }

        authResultState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                withTimeout(FIREBASE_TIMEOUT) {
                    // Create user account
                    val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                    val user = authResult.user
                    if (user != null) {
                        try {
                            // Update profile
                            val profileUpdates = userProfileChangeRequest {
                                displayName = fullName
                            }
                            user.updateProfile(profileUpdates).await()

                            // Create user account in database
                            val newAccount = UserAccount(
                                uid = user.uid,
                                fullName = fullName,
                                email = email
                            )
                            userRepository.createOrUpdateUserAccount(newAccount)

                            Log.d(TAG, "Registration successful for email: $email")
                            authResultState.value = AuthResult.Success
                        } catch (e: Exception) {
                            handleError("Failed to complete registration: ${e.localizedMessage}")
                            // Cleanup on failure
                            user.delete().await()
                        }
                    } else {
                        handleError("User registration failed")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters"
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                    is FirebaseAuthUserCollisionException -> "Account already exists"
                    is FirebaseException -> "Network error. Please check your connection"
                    is CancellationException -> "Registration timed out"
                    else -> e.localizedMessage ?: "Registration failed"
                }
                handleError(errorMessage)
            }
        }
    }

    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        when {
            fullName.length < 2 -> {
                handleError("Name must be at least 2 characters long")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                handleError("Invalid email format")
                return false
            }
            password.length < 6 -> {
                handleError("Password must be at least 6 characters long")
                return false
            }
            password != confirmPassword -> {
                handleError("Passwords do not match")
                return false
            }
            else -> return true
        }
    }

    private fun handleError(message: String) {
        Log.e(TAG, message)
        authResultState.value = AuthResult.Error(message)
    }

    fun resetState() {
        authResultState.value = AuthResult.Idle
    }
}