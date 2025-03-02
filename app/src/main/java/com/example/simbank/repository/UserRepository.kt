package com.example.simbank.repository

import com.example.simbank.datamodels.Transaction
import com.example.simbank.datamodels.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import android.util.Log
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Repository class for handling user data operations.
 */
class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Listens in real time to changes to the user's Firestore document.
     * Converts the Firestore document to a UserAccount object and sends updates automatically to the UI that is listening.
     *
     * @return A Flow emitting UserAccount objects or null if the user is not authenticated.
     */
    fun getUserAccount(): Flow<UserAccount?> {
        val uid = auth.currentUser?.uid ?: return flowOf(null)
        val docRef = db.collection("users").document(uid)

        return callbackFlow {
            val subscription = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val account = snapshot?.toObject(UserAccount::class.java)
                trySend(account)
            }
            awaitClose { subscription.remove() }
        }
    }

    /**
     * Fetches the user's Firestore document once and converts it to a UserAccount object.
     *
     * @param uid The unique identifier of the user.
     * @return The UserAccount object or null if the fetch fails.
     */
    suspend fun getUserAccountOnce(uid: String): UserAccount? {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.toObject(UserAccount::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch user account: ${e.message}")
            null
        }
    }

    /**
     * Ensures the user has an account document in Firestore.
     * If not, it creates one. If it does, it updates the document with the new account data.
     *
     * @param account The UserAccount object containing the account data.
     */
    suspend fun createOrUpdateUserAccount(account: UserAccount) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).set(account).await()
    }

    /**
     * Gets the user's current balance, calculates the new balance based on the transaction type,
     * creates a new transaction record, and updates the Firestore document with the new account data.
     *
     * @param amount The amount of the transaction.
     * @param type The type of the transaction (e.g., deposit or withdrawal).
     */
    suspend fun performTransaction(amount: Double, type: String) {
        if (amount <= 0) {
            throw IllegalArgumentException("Amount must be positive")
        }

        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("users").document(uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentAccount = snapshot.toObject(UserAccount::class.java) ?: UserAccount(uid = uid)

            // Validate withdrawal amount
            if (type == "withdrawal" && currentAccount.balance < amount) {
                throw IllegalStateException("Insufficient funds")
            }

            val newBalance = when (type) {
                "deposit" -> currentAccount.balance + amount
                "withdrawal" -> currentAccount.balance - amount
                else -> throw IllegalArgumentException("Invalid transaction type")
            }

            val newTransaction = Transaction(amount = amount, type = type)
            val updatedTransactions = currentAccount.transactions + newTransaction

            val updatedAccount = currentAccount.copy(
                balance = newBalance,
                transactions = updatedTransactions
            )

            transaction.set(docRef, updatedAccount)
            updatedAccount
        }.await()
    }
}