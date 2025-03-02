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
import com.example.simbank.viewmodel.RegisterAuthViewModel

/**
 * Composable function for the Register Screen.
 *
 * @param navController The NavController for navigation.
 * @param registerAuthViewModel The ViewModel for handling registration authentication.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    registerAuthViewModel: RegisterAuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authResultState = registerAuthViewModel.authResultState.value
    val context = LocalContext.current
    val TAG = "RegisterScreen"

    // Handle side effects based on the authentication result.
    when (authResultState) {
        is AuthResult.Success -> {
            LaunchedEffect(Unit) {
                Log.d(TAG, "Registration successful, navigating to SendCode screen.")
                navController.navigate(Screen.SendCode.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
        is AuthResult.Error -> {
            LaunchedEffect(authResultState) {
                Log.e(TAG, "Registration error: ${authResultState.message}")
                Toast.makeText(context, authResultState.message, Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Loading -> {
            LaunchedEffect(authResultState) {
                Log.d(TAG, "Registration loading...")
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }
        }
        AuthResult.Idle -> {
            Log.d(TAG, "Registration idle.")
        }
    }

    // Call the stateless UI component.
    RegisterContent(
        fullName = fullName,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        onFullNameChange = { fullName = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onRegisterClick = {
            Log.d(TAG, "Register button clicked with email: $email")
            registerAuthViewModel.registerUser(fullName, email, password, confirmPassword)
        },
        onLoginClick = {
            Log.d(TAG, "Navigating to Login screen.")
            navController.navigate(Screen.Login.route)
        }
    )
}

/**
 * Composable function for displaying the registration content.
 *
 * @param fullName The full name entered by the user.
 * @param email The email address entered by the user.
 * @param password The password entered by the user.
 * @param confirmPassword The confirmation password entered by the user.
 * @param onFullNameChange Callback when the full name changes.
 * @param onEmailChange Callback when the email address changes.
 * @param onPasswordChange Callback when the password changes.
 * @param onConfirmPasswordChange Callback when the confirmation password changes.
 * @param onRegisterClick Callback when the register button is clicked.
 * @param onLoginClick Callback when the login text is clicked.
 */
@Composable
fun RegisterContent(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable { onLoginClick() },
            style = MaterialTheme.typography.body1
        )
    }
}

/**
 * Preview function for RegisterContent composable.
 */
@Preview(showBackground = true)
@Composable
fun PreviewRegisterContent() {
    RegisterContent(
        fullName = "John Doe",
        email = "john@example.com",
        password = "password",
        confirmPassword = "password",
        onFullNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRegisterClick = {},
        onLoginClick = {}
    )
}