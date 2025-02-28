package com.example.simbank.ui

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
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Welcome, User!", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.AccountPage.route) },
            elevation = 4.dp
        ) {
            Text(
                text = "Balance: $17,380.42",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { navController.navigate(Screen.Deposit.route) }) { Text(text = "Deposit") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { navController.navigate(Screen.SendMoney.route) }) { Text(text = "Send") }
        }
        Button(onClick = { navController.navigate(Screen.Login.route) }) { Text(text = "Logout") }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = NavController(LocalContext.current))
}