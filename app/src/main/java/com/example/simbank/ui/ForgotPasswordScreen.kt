package com.example.simbank.ui

import ForgotPassAuthViewModel
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
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.AuthResult

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    forgotPassAuthViewModel: ForgotPassAuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }

    val authResultState = forgotPassAuthViewModel.authResultState.value
    val context = LocalContext.current
    val TAG = "ForgotPasswordScreen"

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
            Log.d(TAG, "Reset idle.")
            // Do nothing
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Forgot Password?", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            Log.d(TAG, "Send Reset Link clicked with email: $email")
            forgotPassAuthViewModel.resetPassword(email)
        }) {
            Text(text = "Send Reset Link")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Back to Login",
            modifier = Modifier.clickable { navController.navigate(Screen.Login.route) },
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    val context = LocalContext.current
    ForgotPasswordScreen(navController = NavController(context))
}