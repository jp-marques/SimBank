package com.example.simbank.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simbank.datamodels.UserAccount
import com.example.simbank.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * ViewModel for handling login authentication.
 */
class LoginAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val TAG = "com.example.simbank.viewmodel.LoginAuthViewModel"

    // Expose login state to the UI
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    /**
     * Initiates the login process for the given email and password.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    fun loginUser(email: String, password: String) {
        Log.d(TAG, "loginUser called with email: $email")

        // Basic validation
        if (email.isBlank() || password.isBlank()) {
            Log.e(TAG, "Validation failed: Email and password are required.")
            authResultState.value = AuthResult.Error("Email and password are required.")
            return
        }

        // Show loading
        authResultState.value = AuthResult.Loading
        Log.d(TAG, "Validation passed, starting Firebase login.")

        // Firebase signInWithEmailAndPassword
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if(user != null) {
                        viewModelScope.launch {
                            Log.d(TAG, "Retrieving user account from repository for user ID: ${user.uid}")
                            val existingAccount = userRepository.getUserAccountOnce(user.uid)
                            Log.d(TAG, "Existing account retrieved: $existingAccount")

                            val updatedAccount = if (existingAccount != null) {
                                Log.d(TAG, "Updating existing account for user ID: ${user.uid}")
                                existingAccount.copy(
                                    fullName = user.displayName ?: "",
                                    email = email)
                            } else {
                                Log.d(TAG, "Creating new account for user ID: ${user.uid}")
                                UserAccount(
                                    uid = user.uid,
                                    fullName = user.displayName ?: "",
                                    email = email)
                            }
                            Log.d(TAG, "Account to be created/updated: $updatedAccount")
                            userRepository.createOrUpdateUserAccount(updatedAccount)
                            authResultState.value = AuthResult.Success
                        }
                        Log.d(TAG, "Login successful for email: $email and user ID: ${user.uid}")
                    }
                } else {
                    // Login failed
                    val errorMsg = task.exception?.localizedMessage ?: "Login failed."
                    Log.e(TAG, "Login failed for email: $email, error: $errorMsg")
                    authResultState.value = AuthResult.Error(errorMsg)
                }
            }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        try{
            FirebaseAuth.getInstance().signOut()
            authResultState.value = AuthResult.Idle
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: ${e.message}")
        }
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return True if a user is authenticated, false otherwise.
     */
    fun checkAuthState(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}