package com.example.simbank.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

    DisposableEffect(Unit) {
        onDispose {
            registerAuthViewModel.resetState()
        }
    }

    when (authResultState) {
        is AuthResult.Success -> {
            LaunchedEffect(Unit) {
                Log.d(TAG, "Registration successful, navigating to SendCode screen.")
                registerAuthViewModel.resetState()
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
        is AuthResult.Loading -> {
            // Show loading indicator
        }
        else -> { /* Do nothing */ }
    }

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
            registerAuthViewModel.registerUser(fullName, email, password, confirmPassword)
        },
        onLoginClick = {
            registerAuthViewModel.resetState()
            navController.navigate(Screen.Login.route)
        },
        authResultState = authResultState
    )
}

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
    onLoginClick: () -> Unit,
    authResultState: AuthResult,
    initialHasInteracted: Boolean = false
) {
    var hasFullNameInteracted by remember { mutableStateOf(initialHasInteracted) }
    var hasPasswordInteracted by remember { mutableStateOf(initialHasInteracted) }
    var hasConfirmPasswordInteracted by remember { mutableStateOf(initialHasInteracted) }
    var hasEmailInteracted by remember { mutableStateOf(initialHasInteracted) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    val emailValidator = remember { mutableStateOf("") }

    LaunchedEffect(emailValidator.value) {
        if (hasEmailInteracted) {
            isEmailError = when {
                emailValidator.value.isBlank() -> {
                    emailError = "Please enter your email address"
                    true
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(emailValidator.value).matches() -> {
                    emailError = "Enter a valid email"
                    true
                }
                else -> false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                onFullNameChange(it)
                hasFullNameInteracted = true
            },
            label = { Text("Full Name") },
            isError = hasFullNameInteracted && (fullName.length < 2 || fullName.any { it.isDigit() || (it !in setOf('-', '\'', ' ') && !it.isLetter()) }),
            supportingText = {
                if (hasFullNameInteracted) {
                    when {
                        fullName.length < 2 -> Text(
                        text = "Name must be at least 2 characters",
                        color = MaterialTheme.colorScheme.error
                    )
                        fullName.any { it.isDigit() || (it !in setOf('-', '\'', ' ') && !it.isLetter()) } -> Text(
                            text = "Invalid characters",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                emailValidator.value = it
                hasEmailInteracted = true
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isEmailError && hasEmailInteracted,
            supportingText = {
                if (isEmailError && hasEmailInteracted) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isEmailError && hasEmailInteracted) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = emailError,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                hasPasswordInteracted = true
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = hasPasswordInteracted && password.length < 6,
            supportingText = {
                if (hasPasswordInteracted && password.length < 6) {
                    Text(
                        text = "Password must be at least 6 characters",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                onConfirmPasswordChange(it)
                hasConfirmPasswordInteracted = true
            },
            label = { Text("Confirm Password") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = hasConfirmPasswordInteracted && password != confirmPassword,
            supportingText = {
                if (hasConfirmPasswordInteracted && password != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            enabled = !isEmailError && fullName.length >= 2 && password.length >= 6 && password == confirmPassword && hasEmailInteracted && hasFullNameInteracted && hasPasswordInteracted && hasConfirmPasswordInteracted && authResultState !is AuthResult.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable(onClick = onLoginClick),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
/**
 * Preview function for RegisterScreen content.
 */
@Preview(showBackground = true)
@Composable
fun PreviewRegisterContent() {
    RegisterContent(
        fullName = "",
        email = "",
        password = "",
        confirmPassword = "",
        onFullNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRegisterClick = {},
        onLoginClick = {},
        authResultState = AuthResult.Idle
    )
}


/**
 * Preview function for RegisterScreen fields with errors.
 */
@Preview(showBackground = true)
@Composable
fun PreviewRegisterFieldsWithErrors() {
    RegisterContent(
        fullName = "J",
        email = "invalid.email",
        password = "123",
        confirmPassword = "456",
        onFullNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRegisterClick = {},
        onLoginClick = {},
        authResultState = AuthResult.Idle,
        initialHasInteracted = true,
    )
}