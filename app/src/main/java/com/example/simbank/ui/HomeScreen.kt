package com.example.simbank.ui

import java.text.NumberFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.LoginAuthViewModel
import com.example.simbank.viewmodel.TransactionViewModel

/**
 * Composable function for the Home Screen.
 *
 * @param navController The NavController for navigation.
 * @param transactionViewModel The ViewModel for handling transactions.
 * @param loginAuthViewModel The ViewModel for handling login authentication.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    loginAuthViewModel: LoginAuthViewModel = viewModel()
) {
    // Extract the balance from the viewmodel.
    val userAccount by transactionViewModel.userAccount.collectAsState()
    val currentBalance = userAccount?.balance ?: 0.0

    HomeContent(
        currentBalance = currentBalance,
        onAccountClick = { navController.navigate(Screen.AccountPage.route) },
        onDepositClick = { navController.navigate(Screen.Deposit.route) },
        onSendClick = { navController.navigate(Screen.SendMoney.route) },
        onLogoutClick = {
            loginAuthViewModel.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

/**
 * Composable function for displaying the home content.
 *
 * @param currentBalance The current balance of the user.
 * @param onAccountClick Callback when the account card is clicked.
 * @param onDepositClick Callback when the deposit button is clicked.
 * @param onSendClick Callback when the send button is clicked.
 * @param onLogoutClick Callback when the logout button is clicked.
 */
@Composable
fun HomeContent(
    currentBalance: Double,
    onAccountClick: () -> Unit,
    onDepositClick: () -> Unit,
    onSendClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Welcome, User!", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAccountClick() },
            elevation = 4.dp
        ) {
            val formattedBalance = NumberFormat.getCurrencyInstance().format(currentBalance)
            Text(
                text = "Balance: $formattedBalance",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onDepositClick) { Text(text = "Deposit") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onSendClick) { Text(text = "Send") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogoutClick) { Text(text = "Logout") }
    }
}

/**
 * Preview function for HomeContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewHomeContent() {
    HomeContent(
        currentBalance = 17380.42,
        onAccountClick = {},
        onDepositClick = {},
        onSendClick = {},
        onLogoutClick = {}
    )
}