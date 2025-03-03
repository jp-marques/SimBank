package com.example.simbank.datamodels

/**
 * Data class representing a user account.
 *
 * @property uid The unique identifier for the user.
 * @property fullName The full name of the user.
 * @property email The email address of the user.
 * @property phoneNumber The phone number of the user.
 * @property balance The current balance of the user's account.
 * @property transactions The list of transactions associated with the user's account.
 */
data class UserAccount(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList()
//    val createdAt: Long = System.currentTimeMillis() //Unsure if i want to implement this
)

/**
 * Data class representing a transaction.
 *
 * @property amount The amount of the transaction.
 * @property type The type of the transaction (e.g., deposit or payment).
 * @property timestamp The timestamp when the transaction occurred.
 */
data class Transaction(
    val amount: Double = 0.0,
    val type: String = "", // deposit or payment
    val timestamp: Long = System.currentTimeMillis()
)

data class TransactionResult(
    val success: Boolean,
    val errorMessage: String? = null
)