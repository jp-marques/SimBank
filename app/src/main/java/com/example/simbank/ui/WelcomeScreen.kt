package com.example.simbank.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simbank.navigation.Screen
import com.example.simbank.theme.SimBankTheme
import com.example.simbank.theme.DarkBlueAccent
import com.example.simbank.theme.ElectricBlueAccent
import com.example.simbank.theme.WhiteBG

@Composable
fun WelcomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueAccent)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Text
        Text(
            text = "Welcome To",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = ElectricBlueAccent,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = "SimBank",
            style = MaterialTheme.typography.displayMedium,
            color = ElectricBlueAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Description Text
        Text(
            text = "Experience secure, intuitive financial management in one place. " +
                    "Track expenses, set savings goals, and stay in control—all with ease.",
            style = MaterialTheme.typography.bodyMedium,
            color = ElectricBlueAccent,
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Get Started Button
        Button(
            onClick = { navController.navigate(Screen.Register.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlueAccent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Get Started",
                color = DarkBlueAccent,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    SimBankTheme {
        WelcomeScreen(navController = rememberNavController())
    }
}