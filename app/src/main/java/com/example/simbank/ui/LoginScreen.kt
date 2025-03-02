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
import com.example.simbank.navigation.Screen
import com.example.simbank.viewmodel.AuthResult
import com.example.simbank.viewmodel.LoginAuthViewModel

/**
 * Composable function for the Login Screen.
 *
 * @param navController The NavController for navigation.
 * @param loginAuthViewModel The ViewModel for handling login authentication.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    loginAuthViewModel: LoginAuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authResultState = loginAuthViewModel.authResultState.value
    val context = LocalContext.current
    val TAG = "LoginScreen"

    // Handle side effects from authResultState
    when (authResultState) {
        is AuthResult.Success -> {
            LaunchedEffect(Unit) {
                Log.d(TAG, "Login successful, navigating to Home screen.")
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        is AuthResult.Error -> {
            LaunchedEffect(authResultState) {
                Log.e(TAG, "Login error: ${authResultState.message}")
                Toast.makeText(context, authResultState.message, Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Loading -> {
            LaunchedEffect(authResultState) {
                Log.d(TAG, "Login loading...")
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Idle -> {
            // Do nothing
        }
    }

    LoginContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = {
            Log.d(TAG, "Login button clicked with email: $email")
            loginAuthViewModel.loginUser(email, password)
        },
        onForgotClick = { navController.navigate(Screen.ForgotPass.route) },
        onRegisterClick = { navController.navigate(Screen.Register.route) },
        authResultState = authResultState
    )
}

/**
 * Composable function for displaying the login content.
 *
 * @param email The email address entered by the user.
 * @param password The password entered by the user.
 * @param onEmailChange Callback when the email address changes.
 * @param onPasswordChange Callback when the password changes.
 * @param onLoginClick Callback when the login button is clicked.
 * @param onForgotClick Callback when the forgot password text is clicked.
 * @param onRegisterClick Callback when the register text is clicked.
 * @param authResultState The current authentication result state.
 */
@Composable
fun LoginContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotClick: () -> Unit,
    onRegisterClick: () -> Unit,
    authResultState: AuthResult
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Forgot password?",
            modifier = Modifier.clickable { onForgotClick() },
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginClick) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Register now",
            modifier = Modifier.clickable { onRegisterClick() },
            style = MaterialTheme.typography.body1
        )
    }
}

/**
 * Preview function for LoginContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewLoginContent() {
    LoginContent(
        email = "test@example.com",
        password = "password",
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onForgotClick = {},
        onRegisterClick = {},
        authResultState = AuthResult.Idle
    )
}