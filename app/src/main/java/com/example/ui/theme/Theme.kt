package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = JeevanNavyLight,
    onPrimary = Color.White,
    primaryContainer = JeevanNavyPrimary,
    onPrimaryContainer = Color.White,
    secondary = JeevanTealAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF003833),
    onSecondaryContainer = Color(0xFF6FFFE9),
    tertiary = JeevanAmberWarning,
    background = JeevanBackgroundDark,
    onBackground = JeevanTextPrimaryDark,
    surface = JeevanSurfaceDark,
    onSurface = JeevanTextPrimaryDark,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = JeevanTextSecondaryDark,
    outline = JeevanCardBorderDark,
    error = JeevanRoseError
)

private val LightColorScheme = lightColorScheme(
    primary = JeevanNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = JeevanBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8EA),
    onSecondaryContainer = Color(0xFF002022),
    tertiary = JeevanAmberWarning,
    background = JeevanBackgroundLight,
    onBackground = JeevanTextPrimaryLight,
    surface = JeevanSurfaceLight,
    onSurface = JeevanTextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = JeevanTextSecondaryLight,
    outline = JeevanCardBorderLight,
    error = JeevanRoseError
)

@Composable
fun JeevanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
