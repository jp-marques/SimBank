package com.example.simbank.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.R
import com.example.simbank.ui.components.TransactionResultPopup
import com.example.simbank.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat

/**
 * Composable function for the Deposit Screen.
 *
 * @param navController The NavController for navigation.
 * @param transactionViewModel The ViewModel for handling transactions.
 */
@Composable
fun DepositScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    // Extract the user account state from the viewmodel.
    val userAccount by transactionViewModel.userAccount.collectAsStateWithLifecycle()
    val currentBalance = userAccount?.balance ?: 0.0
    var showPopup by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var autoHideJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        DepositContent(
            currentBalance = currentBalance,
            onDeposit = { amount ->
                transactionViewModel.viewModelScope.launch {
                    isSuccess = transactionViewModel.deposit(amount)
                    showPopup = true
                    autoHideJob = scope.launch {
                        delay(2500)
                        navController.popBackStack()
                        // Delay the popup state update slightly
                        delay(100)
                        showPopup = false
                    }
                }
            },
            onBack = { navController.popBackStack() }
        )

        AnimatedVisibility(
            visible = showPopup,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        autoHideJob?.cancel()
                        navController.popBackStack()
                        scope.launch {
                            delay(100)
                            showPopup = false
                        }
                    }
            ) {
                TransactionResultPopup(
                    isSuccess = isSuccess,
                    onDismiss = {
                        autoHideJob?.cancel()
                        navController.popBackStack()
                        scope.launch {
                            delay(100)
                            showPopup = false
                        }
                    }
                )
            }
        }
    }
}

/**
 * Composable function for displaying the deposit content.
 *
 * @param currentBalance The current balance of the user.
 * @param onDeposit Callback when the deposit button is clicked.
 * @param onBack Callback when the back button is clicked.
 */
@Composable
fun DepositContent(
    currentBalance: Double,
    onDeposit: (Double) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Deposit funds") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val formattedBalance = NumberFormat.getCurrencyInstance().format(currentBalance)
            Text(text = "Current balance: $formattedBalance", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))
            var amount by remember { mutableStateOf("") }
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter the amount you want to deposit") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (amount.isNotBlank()) onDeposit(amount.toDouble())
            }) {
                Text(text = "Deposit")
            }
        }
    }
}

/**
 * Preview function for DepositContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewDepositContent() {
    // Preview the stateless component with hard-coded data.
    DepositContent(
        currentBalance = 1000.0,
        onDeposit = {},
        onBack = {}
    )
}