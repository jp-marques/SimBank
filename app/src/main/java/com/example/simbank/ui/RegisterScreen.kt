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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            Log.d(TAG, "Register button clicked with email: $email")
            registerAuthViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
        }) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable {
                Log.d(TAG, "Navigating to Login screen.")
                navController.navigate(Screen.Login.route)
            },
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    val context = LocalContext.current
    RegisterScreen(navController = NavController(context))
}