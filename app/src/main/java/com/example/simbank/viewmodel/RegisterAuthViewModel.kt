package com.example.simbank.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simbank.datamodels.UserAccount
import com.example.simbank.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch

/**
 * ViewModel for handling registration authentication.
 */
class RegisterAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val TAG = "AuthViewModel"

    // Expose registration state to the UI
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    /**
     * Initiates the registration process for the given user details.
     *
     * @param fullName The full name of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     * @param confirmPassword The confirmation password entered by the user.
     */
    fun registerUser(fullName: String, email: String, password: String, confirmPassword: String) {
        Log.d(TAG, "registerUser called with email: $email")

        // Basic validation
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            Log.e(TAG, "Validation failed: All fields are required.")
            authResultState.value = AuthResult.Error("All fields are required.")
            return
        }
        if (password != confirmPassword) {
            Log.e(TAG, "Validation failed: Passwords do not match.")
            authResultState.value = AuthResult.Error("Passwords do not match.")
            return
        }

        // Show loading
        authResultState.value = AuthResult.Loading
        Log.d(TAG, "Validation passed, starting Firebase registration.")

        // Firebase createUserWithEmailAndPassword
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if(user != null) {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = fullName
                        }
                        user.updateProfile(profileUpdates).addOnCompleteListener{
                            if (it.isSuccessful) {
                                Log.d(TAG, "User profile updated with name: $fullName")
                            } else {
                                Log.e(TAG, "Failed to update user profile: ${it.exception?.localizedMessage}")
                            }
                        }

                        val newAccount = UserAccount(
                            uid = user.uid,
                            fullName = fullName,
                            email = email)
                        viewModelScope.launch {
                            userRepository.createOrUpdateUserAccount(newAccount)
                        }
                        Log.d(TAG, "Registration successful for email: $email")
                    }
                    authResultState.value = AuthResult.Success
                } else {
                    // Registration failed
                    val errorMsg = task.exception?.localizedMessage ?: "Registration failed."
                    Log.e(TAG, "Registration failed for email: $email, error: $errorMsg")
                    authResultState.value = AuthResult.Error(errorMsg)
                }
            }
    }
}