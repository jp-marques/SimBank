package com.example.simbank.ui

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
fun SendCodeScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Verify Your Phone Number", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Enter your phone number") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.OtpRegister.route) }) {
            Text(text = "Send Code")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSendCodeScreen() {
    SendCodeScreen(navController = NavController(LocalContext.current))
}