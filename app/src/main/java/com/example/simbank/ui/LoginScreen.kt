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
import androidx.compose.material.icons.filled.Warning
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
import com.example.simbank.viewmodel.LoginAuthViewModel
import kotlinx.coroutines.delay


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

    var hasInteracted by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    val emailValidator = remember { mutableStateOf("") }

    LaunchedEffect(emailValidator.value) {
        if (hasInteracted) {
//            delay(500)
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
        Text(text = "Login", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                emailValidator.value = it
                hasInteracted = true
            },
            label = { Text("Email")
            },
            singleLine = true,
            isError = isEmailError,
            supportingText = {
                if(isEmailError) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1
                    )
                }
            },
            trailingIcon = {
                if (isEmailError) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = emailError,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Forgot password?",
            modifier = Modifier.clickable { onForgotClick() },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginClick) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Register now",
            modifier = Modifier.clickable { onRegisterClick() },
            style = MaterialTheme.typography.bodyMedium
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

@Preview(showBackground = true)
@Composable
fun PreviewLoginFieldsWithErrors() {
    var email by remember { mutableStateOf("test") }
    var password by remember { mutableStateOf("123") }
    var emailError by remember { mutableStateOf("Invalid email format") }
    var passwordError by remember { mutableStateOf("Password must be at least 6 characters") }
    var isEmailError by remember { mutableStateOf(true) }
    var isPasswordError by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = isEmailError,
            supportingText = {
                if (isEmailError) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isEmailError) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = emailError,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            isError = isPasswordError,
//            supportingText = {
//                if (isPasswordError) {
//                    Text(
//                        text = passwordError,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            },
//            trailingIcon = {
//                if (isPasswordError) {
//                    Icon(
//                        imageVector = Icons.Filled.Error,
//                        contentDescription = passwordError,
//                        tint = MaterialTheme.colorScheme.error
//                    )
//                } else {
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(
//                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
//                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
//                        )
//                    }
//                }
//            },
//            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}