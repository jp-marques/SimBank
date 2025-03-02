package com.example.simbank.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

data class AuthPhoneResult(
    val isCodeSent: Boolean = false,
    val isSignedIn: Boolean = false,
    val errorMessage: String? = null
)

class PhoneVerifyAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "PhoneVerifyAuthViewModel"
    private var storedVerificationId: String? = null

    val authPhoneResultState = mutableStateOf<AuthPhoneResult>(AuthPhoneResult())

    fun startPhoneVerification(phoneNumber: String) {}
}