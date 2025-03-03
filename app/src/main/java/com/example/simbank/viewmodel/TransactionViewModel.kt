package com.example.simbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.simbank.datamodels.TransactionResult
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
     * @return TransactionResult containing success status and error message if any.
     */
    suspend fun deposit(amount: Double): TransactionResult {
        return try {
            if (amount <= 0) {
                return TransactionResult(false, "Deposit amount must be positive")
            }

            val account = _userAccount.value
                ?: return TransactionResult(false, "User account data is not loaded")

            if(account.uid.isNullOrEmpty()) {
                return TransactionResult(false, "Invalid User Account Data")
            }

            if (!amount.isFinite() || amount.isNaN()) {
                return TransactionResult(false, "Deposit amount is not a valid number")
            }

            userRepository.performTransaction(amount, "deposit")
            TransactionResult(true)
        } catch (e: IllegalStateException) {
            Log.e("TransactionViewModel", "Insufficient funds: ${e.message}")
            TransactionResult(false, e.message)
        } catch (e: Exception) {
            Log.e("TransactionViewModel", "Failed to perform deposit: ${e.message}", e)
            TransactionResult(false, e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Withdraws the specified amount from the user's account.
     *
     * @param amount The amount to withdraw.
     * @return True if the transaction succeeded, false otherwise.
     */
    suspend fun withdraw(amount: Double): TransactionResult {
        return try {
            if (amount <= 0) {
                return TransactionResult(false, "Payment amount must be positive")
            }

            val account = _userAccount.value
                ?: return TransactionResult(false, "User account data is not loaded")

            if(account.uid.isNullOrEmpty()) {
                return TransactionResult(false, "Invalid User Account Data")
            }

            if (!amount.isFinite() || amount.isNaN()) {
                return TransactionResult(false, "Payment amount is not a valid number")
            }

            if (amount > account.balance) {
                return TransactionResult(false, "Insufficient funds")
            }

            userRepository.performTransaction(amount, "withdrawal")
            TransactionResult(true)
        } catch (e: Exception) {
            Log.e("TransactionViewModel", "Failed to perform payment: ${e.message}", e)
            TransactionResult(false, e.message ?: "Unknown error occurred")
        }
    }
}