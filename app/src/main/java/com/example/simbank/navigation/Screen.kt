package com.example.simbank.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Register : Screen("register")
    object OtpRegister : Screen("otp")
    object Login : Screen("login")
    object Home : Screen("home")
    object Deposit : Screen("deposit")
    object SendMoney : Screen("send_money")
    object VerifyPhone : Screen("send_code")
    object ForgotPass : Screen("forgot_pass")
    object AccountPage : Screen("account_page")
}
