package com.example.simbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.example.simbank.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.simbank.navigation.AppNavigation
import com.example.simbank.theme.SimBankTheme
import com.example.simbank.viewmodel.LoginAuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimBankTheme {
                val navController = rememberNavController()
                val loginAuthViewModel: LoginAuthViewModel = viewModel()

                LaunchedEffect(Unit) {
                    if (!loginAuthViewModel.checkAuthState()) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                AppNavigation(navController)
            }
        }
    }
}