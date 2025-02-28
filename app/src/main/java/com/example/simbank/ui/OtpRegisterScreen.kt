package com.example.simbank.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simbank.navigation.Screen

@Composable
fun OtpRegisterScreen(navController: NavController) {
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Enter Verification Code", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("6-digit OTP") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text(text = "Verify")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Didn't receive the code? Resend",
            modifier = Modifier.clickable {
                Toast.makeText(context, "Code resent", Toast.LENGTH_SHORT).show()
            },
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOtpScreen() {
    val context = LocalContext.current
    OtpRegisterScreen(navController = NavController(context))
}