package com.example.simbank.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simbank.R
import com.example.simbank.ui.components.TransactionResultPopup
import com.example.simbank.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Composable function for the Payment Screen.
 *
 * @param navController The NavController for navigation.
 * @param transactionViewModel The ViewModel for handling transactions.
 */
@Composable
fun PaymentScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val userAccount by transactionViewModel.userAccount.collectAsStateWithLifecycle()
    val currentBalance = userAccount?.balance ?: 0.0
    var showPopup by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var autoHideJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        PaymentContent(
            currentBalance = currentBalance,
            onSend = { amount ->
                transactionViewModel.viewModelScope.launch {
                    val result = transactionViewModel.withdraw(amount)
                    isSuccess = result.success
                    errorMessage = result.errorMessage
                    showPopup = true
                    autoHideJob = scope.launch {
                        delay(2500)
                        navController.popBackStack()
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
            TransactionResultPopup(
                isSuccess = isSuccess,
                errorMessage = errorMessage,
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
/**
 * Composable function for displaying the payment content.
 *
 * @param currentBalance The current balance of the user.
 * @param onSend Callback when the send button is clicked.
 * @param onBack Callback when the back button is clicked.
 */
@Composable
fun PaymentContent(
    currentBalance: Double,
    onSend: (Double) -> Unit,
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isError by remember { mutableStateOf(false) }

    // Validation function
    fun validateAmount(input: String): Boolean {
        return try {
            when {
                input.isEmpty() -> {
                    errorMessage = "Amount cannot be empty"
                    false
                }
                input.count { it == '.' } > 1 -> {
                    errorMessage = "Invalid number format"
                    false
                }
                input.contains(Regex("[^0-9.]")) -> {
                    errorMessage = "Only numbers are allowed"
                    false
                }
                input.substringAfter('.', "").length > 2 -> {
                    errorMessage = "Maximum 2 decimal places allowed"
                    false
                }
                input.toDouble() <= 0 -> {
                    errorMessage = "Amount must be greater than zero"
                    false
                }
                input.toDouble() > currentBalance -> {
                    errorMessage = "Insufficient funds"
                    false
                }
                else -> {
                    errorMessage = ""
                    true
                }
            }
        } catch (e: NumberFormatException) {
            errorMessage = "Invalid number format"
            false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Send Money") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
            Text(
                text = "Current balance: $formattedBalance",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    isError = !validateAmount(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Amount") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                supportingText = {
                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = if (isError) {
                    {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateAmount(amount)) {
                        onSend(amount.toDouble())
                    } else {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = amount.isNotBlank() && errorMessage.isEmpty()
            ) {
                Text(text = "Send")
            }
        }
    }
}

/**
 * Preview function for PaymentContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewPaymentContent() {
    PaymentContent(
        currentBalance = 10000.0,
        onSend = { },
        onBack = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTextField_NoError() {
    MaterialTheme {
        OutlinedTextField(
            value = "100.00",
            onValueChange = {},
            label = { Text("Amount") },
            prefix = { Text("$") },
            modifier = Modifier.padding(16.dp),
            isError = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTextField_WithError() {
    MaterialTheme {
        OutlinedTextField(
            value = "-100",
            onValueChange = {},
            label = { Text("Amount") },
            prefix = { Text("$") },
            modifier = Modifier.padding(16.dp),
            isError = true,
            supportingText = {
                Text(
                    text = "Amount must be greater than zero",
                    color = MaterialTheme.colorScheme.error
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}