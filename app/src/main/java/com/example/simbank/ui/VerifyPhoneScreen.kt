package com.example.simbank.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simbank.navigation.Screen
import com.example.simbank.utils.PhoneNumberVisualTransformation
import com.example.simbank.viewmodel.PhoneVerifyAuthViewModel

@Composable
fun VerifyPhoneScreen(
    navController: NavController,
    phoneVerifyAuthViewModel: PhoneVerifyAuthViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as Activity

    Box(modifier = Modifier.fillMaxSize()) {
        VerifyPhoneContent(
            phoneNumber = phoneNumber,
            isError = isError,
            errorMessage = errorMessage,
            onPhoneNumberChange = { newNumber ->
                phoneNumber = newNumber
                isError = !validatePhoneNumber(newNumber) { error ->
                    errorMessage = error
                }
            },
            onSendCodeClick = {
//                phoneVerifyAuthViewModel.requestCode(phoneNumber)
                navController.navigate(Screen.OtpRegister.route)
            }
        )
    }
}

@Composable
fun VerifyPhoneContent(
    phoneNumber: String,
    isError: Boolean,
    errorMessage: String?,
    onPhoneNumberChange: (String) -> Unit,
    onSendCodeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Verify Your Phone Number",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isError,
            supportingText = {
                if (isError && !errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
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
            } else null,
            visualTransformation = PhoneNumberVisualTransformation()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSendCodeClick,
            enabled = !isError && phoneNumber.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Send Code")
        }
    }
}

private fun validatePhoneNumber(number: String, onError: (String) -> Unit): Boolean {
    return when {
        number.contains(Regex("[^0-9+]")) -> {
            onError("Phone number can only contain numbers and +")
            false
        }
        number.isNotEmpty() && number.length < 10 -> {
            onError("Phone number is too short")
            false
        }
        number.isNotEmpty() && number.length > 10 -> {
            onError("Phone number is too long")
            false
        }
        else -> {
            onError("")
            true
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerifyPhoneContent() {
    VerifyPhoneContent(
        phoneNumber = "1234567890",
        isError = false,
        errorMessage = null,
        onPhoneNumberChange = {},
        onSendCodeClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewVerifyPhoneContent_WithError() {
    VerifyPhoneContent(
        phoneNumber = "1231",
        isError = true,
        errorMessage = "Phone number is too short",
        onPhoneNumberChange = {},
        onSendCodeClick = {}
    )
}