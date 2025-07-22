package com.smartwellness.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFA3F18F),
    onPrimary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF88C779),
    onPrimary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
)

@Composable
fun SmartWellnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = SmartWellnessTypography,
        content = content
    )
}
