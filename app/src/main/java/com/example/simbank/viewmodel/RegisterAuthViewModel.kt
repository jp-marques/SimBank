package com.example.simbank.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "AuthViewModel"

    // Expose registration state to the UI
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

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
                    // Registration success
                    Log.d(TAG, "Registration successful for email: $email")
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