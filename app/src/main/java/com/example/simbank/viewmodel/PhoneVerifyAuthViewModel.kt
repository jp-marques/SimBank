package com.example.simbank.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simbank.datamodels.UserAccount
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.simbank.datamodels.UserRegistrationData
import com.example.simbank.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AuthPhoneResult(
    val isCodeSent: Boolean = false,
    val isSignedIn: Boolean = false,
    val errorMessage: String? = null
)


@HiltViewModel
class PhoneVerifyAuthViewModel @Inject constructor(
    // Inject any dependencies here if needed, for example:
    // private val someRepository: SomeRepository
) : ViewModel() {
    // Existing implementation remains here


    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val TAG = "PhoneVerifyAuthViewModel"
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val _countdown = MutableStateFlow<Int?>(null)
    val countdown = _countdown.asStateFlow()

    var storedPhoneNumber: String = "a"


    private var countdownJob: kotlinx.coroutines.Job? = null
    val authPhoneResultState = mutableStateOf<AuthPhoneResult>(AuthPhoneResult())

    private val FIREBASE_TIMEOUT = 10000L // 10 seconds


    // TODO: Add temporary storage for registration data that will be combined with phone verification
    private var pendingRegistrationData: UserRegistrationData? = null

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _countdown.value = 60
            while (_countdown.value!! > 0) {
                delay(1000)
                _countdown.value = _countdown.value!! - 1
            }
            _countdown.value = null
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "Verification completed automatically")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
                is FirebaseTooManyRequestsException -> "SMS quota exceeded. Try again later"
                else -> "Verification failed: ${e.localizedMessage}"
            }
            handleError(errorMessage)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d(TAG, "Verification code sent")
            storedVerificationId = verificationId
            resendToken = token
            authPhoneResultState.value = AuthPhoneResult(isCodeSent = true)
        }
    }

    fun requestCode(phoneNumber: String) {

        storedPhoneNumber = phoneNumber
        authPhoneResultState.value = AuthPhoneResult(isCodeSent = false)

        // Validate phone number format
        if (!phoneNumber.matches(Regex("^[+]?[0-9]{10,15}\$"))) {
            handleError("Invalid phone number format")
            return
        }

//        viewModelScope.launch {
//            try {
//                withTimeout(FIREBASE_TIMEOUT) {
//                    val options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                        .setPhoneNumber(phoneNumber)
//                        .setTimeout(60L, TimeUnit.SECONDS)
//                        .setActivity(firebaseAuth.app.applicationContext as android.app.Activity)
//                        .setCallbacks(callbacks)
//                        .build()
//                    PhoneAuthProvider.verifyPhoneNumber(options)
//                    startCountdown()
//                }
//            } catch (e: Exception) {
//                val errorMessage = when (e) {
//                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
//                    is FirebaseTooManyRequestsException -> "SMS quota exceeded. Try again later"
//                    else -> "Failed to send verification code: ${e.localizedMessage}"
//                }
//                handleError(errorMessage)
//            }
//        }
    }

    fun verifyCode(code: String) {


        viewModelScope.launch {
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                val updatedAccount = userRepository.getUserAccountOnce(uid)?.copy(phoneNumber = storedPhoneNumber)
                if (updatedAccount != null){
                    userRepository.createOrUpdateUserAccount(updatedAccount)
                    Log.d(TAG, "verifyCode: Update Account is $updatedAccount")
                }
            }
        }

//        val verificationId = storedVerificationId
//        if (verificationId == null) {
//            handleError("Verification ID not found")
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                withTimeout(FIREBASE_TIMEOUT) {
//                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
//                    signInWithPhoneAuthCredential(credential)
//                }
//            } catch (e: Exception) {
//                handleError("Code verification failed: ${e.localizedMessage}")
//            }
//        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                withTimeout(FIREBASE_TIMEOUT) {
                    firebaseAuth.signInWithCredential(credential).await()
                    authPhoneResultState.value = AuthPhoneResult(isSignedIn = true)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid verification code"
                    else -> "Sign in failed: ${e.localizedMessage}"
                }
                handleError(errorMessage)
            }
        }
    }

    private fun handleError(message: String) {
        Log.e(TAG, message)
        authPhoneResultState.value = AuthPhoneResult(errorMessage = message)
    }

    fun resetState() {
        authPhoneResultState.value = AuthPhoneResult()
        countdownJob?.cancel()
        _countdown.value = null
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}