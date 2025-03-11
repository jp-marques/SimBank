package com.example.simbank.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
// Dark Theme colors
private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueAccent,
    onPrimary = CharcoalPrimary,
    secondary = PinkSecondary,
    onSecondary = WhiteBG,
    tertiary = GreenAccent,
    onTertiary = CharcoalPrimary,
    error = RedAccent,
    onError = WhiteBG,
    background = CharcoalPrimary,
    onBackground = WhiteBG,
    surface = DarkBlueAccent,
    onSurface = WhiteBG,
)

// Light Theme colors
private val LightColorScheme = lightColorScheme(
    primary = ElectricBlueAccent,
    onPrimary = DarkBlueAccent,
    secondary = PinkSecondary,
    onSecondary = CharcoalPrimary,
    tertiary = GreenAccent,
    onTertiary = CharcoalPrimary,
    error = RedAccent,
    onError = WhiteBG,
    background = WhiteBG,
    onBackground = CharcoalPrimary,
    surface = WhiteBG,
    onSurface = CharcoalPrimary,
)

@Composable
fun SimBankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // If you want to disable dynamic color usage, set this to false:
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamically generate color schemes on Android 12+
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Refer to your custom Typography.kt if any
        content = content
    )
}