package com.example.simbank.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.AuthResult
import com.example.simbank.viewmodel.ForgotPassAuthViewModel

/**
 * Composable function for the Forgot Password Screen.
 *
 * @param navController The NavController for navigation.
 * @param forgotPassAuthViewModel The ViewModel for handling forgot password authentication.
 */
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    forgotPassAuthViewModel: ForgotPassAuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val authResultState = forgotPassAuthViewModel.authResultState.value
    val context = LocalContext.current
    val TAG = "ForgotPasswordScreen"

    // Handle side effects from authResultState.
    when (authResultState) {
        is AuthResult.Success -> {
            LaunchedEffect(Unit) {
                Log.d(TAG, "Reset link sent")
                Toast.makeText(context, "Reset link sent", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        is AuthResult.Error -> {
            LaunchedEffect(authResultState) {
                Log.e(TAG, "Reset error: ${authResultState.message}")
                Toast.makeText(context, authResultState.message, Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Loading -> {
            LaunchedEffect(authResultState) {
                Log.d(TAG, "Reset loading...")
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Idle -> {
            // Do nothing.
        }
    }

    // Call the stateless content.
    ForgotPasswordContent(
        email = email,
        onEmailChange = { email = it },
        onResetClick = { forgotPassAuthViewModel.resetPassword(email) },
        onBackClick = { navController.navigate(Screen.Login.route) }
    )
}

/**
 * Composable function for displaying the forgot password content.
 *
 * @param email The email address entered by the user.
 * @param onEmailChange Callback when the email address changes.
 * @param onResetClick Callback when the reset button is clicked.
 * @param onBackClick Callback when the back button is clicked.
 */
@Composable
fun ForgotPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Forgot Password?", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onResetClick) {
            Text(text = "Send Reset Link")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Back to Login",
            modifier = Modifier.clickable { onBackClick() },
            style = MaterialTheme.typography.body1
        )
    }
}

/**
 * Preview function for ForgotPasswordContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordContent() {
    ForgotPasswordContent(
        email = "test@example.com",
        onEmailChange = {},
        onResetClick = {},
        onBackClick = {}
    )
}