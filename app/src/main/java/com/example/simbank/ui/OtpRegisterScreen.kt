package com.example.simbank.ui

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.PhoneVerifyAuthViewModel

@Composable
fun OtpRegisterScreen(
    navController: NavController,
    phoneVerifyAuthViewModel: PhoneVerifyAuthViewModel = hiltViewModel(
        navController.getBackStackEntry("auth")
    )
) {

    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val countdown by phoneVerifyAuthViewModel.countdown.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        OtpRegisterContent(
            otp = otp,
            isError = isError,
            errorMessage = errorMessage,
            countdown = countdown,
            onOtpChange = { newOtp ->
                otp = newOtp
                isError = !validateOtp(newOtp) { errorMsg ->
                    errorMessage = errorMsg
                }
            },
            onVerifyClick = {
                phoneVerifyAuthViewModel.verifyCode(otp)
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.OtpRegister.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onResendClick = {
                if (countdown == null) {
//                    phoneVerifyAuthViewModel.requestCode(//phone number)
                    Toast.makeText(context, "Code resent", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun OtpRegisterContent(
    otp: String,
    isError: Boolean,
    errorMessage: String?,
    countdown: Int?,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enter Verification Code",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = onOtpChange,
            label = { Text("6-digit OTP") },
            modifier = Modifier.fillMaxWidth(),
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
            } else null
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onVerifyClick,
            enabled = otp.length == 6 && !isError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Verify")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Didn't receive the code? ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = countdown?.let { "Resend in ${it}s" } ?: "Resend",
                color = if (countdown == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.clickable(enabled = countdown == null, onClick = onResendClick),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun validateOtp(otp: String, onError: (String) -> Unit): Boolean {
    return when {
        otp.contains(Regex("[^0-9]")) -> {
            onError("OTP must be a number")
            false
        }
        otp.isNotEmpty() && otp.length != 6 -> {
            onError("OTP must be 6 digits")
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
fun PreviewOtpRegisterContent() {
    OtpRegisterContent(
        otp = "123456",
        isError = false,
        errorMessage = null,
        countdown = null,
        onOtpChange = {},
        onVerifyClick = {},
        onResendClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOtpRegisterContent_WithError() {
    OtpRegisterContent(
        otp = "12345",
        isError = true,
        errorMessage = "OTP must be 6 digits",
        countdown = 30,
        onOtpChange = {},
        onVerifyClick = {},
        onResendClick = {}
    )
}