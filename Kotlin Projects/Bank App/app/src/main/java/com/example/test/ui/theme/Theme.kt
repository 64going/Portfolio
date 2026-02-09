package com.example.test.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val DarkColorScheme = darkColorScheme(
    primary = (Color(0xFFBB86FC)),
    secondary = (Color(0xFF03DAC6)),
    tertiary = Pink80,
    onPrimary = Color.Black,
    primaryContainer = (Color(0xFF3700B3)),
    onPrimaryContainer = Color.White,
    onSecondary = Color.Black,
    background = (Color(0xFF121212)),
    onBackground = Color.White,
    surface = (Color(0xFF1E1E1E)),
    onSurface = Color.White,
    error = (Color(0xFFCF6679)),
    onError = Color.Black
    )
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
val LightColorScheme = lightColorScheme(
    primary = (Color(0xFF6200EE)),
    secondary = (Color(0xFF03DAC6)),
    tertiary = Pink40,
    onPrimary = Color.White,
    primaryContainer = (Color(0xFFBB86FC)),
    onPrimaryContainer = Color.Black,
    onSecondary = Color.Black,
    background = (Color(0xFFF0F0F0)),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = (Color(0xFFB00020)),
    onError = Color.White
)

@Composable
fun TestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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