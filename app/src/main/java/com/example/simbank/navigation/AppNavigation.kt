package com.example.simbank.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simbank.ui.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.OtpRegister.route) { OtpRegisterScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Deposit.route) { DepositScreen(navController) }
        composable(Screen.SendMoney.route) { PaymentScreen(navController) }
        composable(Screen.SendCode.route) { SendCodeScreen(navController) }
        composable(Screen.ForgotPass.route) { ForgotPasswordScreen(navController) }
        composable(Screen.AccountPage.route) { AccountPageScreen(navController) }
    }
}
