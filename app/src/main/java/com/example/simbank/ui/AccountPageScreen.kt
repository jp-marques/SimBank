package com.example.simbank.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.R
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.TransactionViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.simbank.datamodels.Transaction
import com.example.simbank.ui.components.TransactionItem
import java.text.NumberFormat

/**
 * Composable function for the Account Page Screen.
 *
 * @param navController The NavController for navigation.
 * @param transactionViewModel The ViewModel for handling transactions.
 */
@Composable
fun AccountPageScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {
    // Extract state from the viewmodel.
    val userAccount by transactionViewModel.userAccount.collectAsState()
    val currentBalance = userAccount?.balance ?: 0.0
    val transactions = userAccount?.transactions ?: emptyList()

    // Use the stateless component.
    AccountPageContent(
        currentBalance = currentBalance,
        transactions = transactions,
        onDepositClick = { navController.navigate(Screen.Deposit.route) },
        onSendClick = { navController.navigate(Screen.SendMoney.route) },
        onBackClick = { navController.popBackStack() }
    )
}

/**
 * Composable function for displaying the account page content.
 *
 * @param currentBalance The current balance of the user.
 * @param onDepositClick Callback when the deposit button is clicked.
 * @param onSendClick Callback when the send button is clicked.
 * @param onBackClick Callback when the back button is clicked.
 * @param transactions List of transactions to display.
 */
@Composable
fun AccountPageContent(
    currentBalance: Double,
    onDepositClick: () -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    transactions: List<Transaction> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Balance") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val formattedBalance = NumberFormat.getCurrencyInstance().format(currentBalance)

            Text(
                text = formattedBalance,
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onDepositClick) {
                    Text(text = "Deposit")
                }
                Button(onClick = onSendClick) {
                    Text(text = "Send")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(8.dp))
            if(transactions.isEmpty()) {
                Text(text = "No transactions yet. Start by making a deposit or sending money!")
            } else {
                LazyColumn {
                    items(transactions.sortedByDescending { it.timestamp }) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }
}

/**
 * Preview function for AccountPageContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewAccountPageContent() {
    AccountPageContent(
        currentBalance = 1000.0,
        onDepositClick = {},
        onSendClick = {},
        onBackClick = {},
        transactions = listOf(
            Transaction(100.0, "deposit", System.currentTimeMillis()),
            Transaction(50.0, "withdrawal", System.currentTimeMillis() - 86400000),
            Transaction(200.0, "deposit", System.currentTimeMillis() - 172800000)
        )
        //dummy transactions
    )
}