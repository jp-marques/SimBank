package com.example.simbank

import android.content.Context
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
                val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
                val loginAuthViewModel: LoginAuthViewModel = viewModel()
                AppNavigation(navController = navController)

//                val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)
                // hardcode to true for testing
                val isFirstLaunch = true
                if (isFirstLaunch) {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                    sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
                } else {
                    LaunchedEffect(Unit) {
                        if (!loginAuthViewModel.checkAuthState()) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                }



            }
        }
    }
}