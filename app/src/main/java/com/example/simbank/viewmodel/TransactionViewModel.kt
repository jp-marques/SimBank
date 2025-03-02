package com.example.simbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.simbank.datamodels.UserAccount
import com.example.simbank.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling transactions.
 *
 * @property userRepository The repository for user data operations.
 */
class TransactionViewModel (
    private val userRepository: UserRepository = UserRepository()
) : ViewModel(){

    // _userAccount is a private variable that stores the latest user data
    // userAccount is a public variable that the UI can observe
    // .collect{} listens to Firestore in real time and updates the state
    private val _userAccount = MutableStateFlow<UserAccount?>(null)
    val userAccount: StateFlow<UserAccount?> = _userAccount

    init {
        viewModelScope.launch {
            userRepository.getUserAccount().collect { account ->
                Log.d("TransactionViewModel", "User account updated: $account")
                _userAccount.value = account
            }
        }
    }

    /**
     * Deposits the specified amount into the user's account.
     *
     * @param amount The amount to deposit.
     * @return True if the transaction succeeded, false otherwise.
     */
    suspend fun deposit(amount: Double): Boolean {
        return try {
            userRepository.performTransaction(amount, "deposit") // Awaits completion
            true  // Transaction succeeded
        } catch (e: Exception) {
            false // Transaction failed
        }
    }

    /**
     * Withdraws the specified amount from the user's account.
     *
     * @param amount The amount to withdraw.
     * @return True if the transaction succeeded, false otherwise.
     */
    suspend fun withdraw(amount: Double): Boolean {
        return try {
            userRepository.performTransaction(amount, "withdrawal")
            true
        } catch (e: Exception) {
            false
        }
    }
}