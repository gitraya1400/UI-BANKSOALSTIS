package com.example.stisbanksoal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Gelap (Kita sesuaikan agar tetap terbaca)
private val DarkColorScheme = darkColorScheme(
    primary = Blue400,    // Versi lebih terang untuk mode gelap
    secondary = Yellow400,
    tertiary = Blue100,
    background = Gray700,
    surface = Gray700,
    onPrimary = Blue900,
    onSecondary = Blue900,
    onBackground = White,
    onSurface = White
)

// Skema Warna Terang (Sesuai request STIS)
private val LightColorScheme = lightColorScheme(
    primary = Blue900,
    secondary = Yellow500,
    tertiary = Blue700,
    background = Blue50,
    surface = White,
    onPrimary = White,
    onSecondary = Blue900,
    onBackground = Blue900,
    onSurface = Blue900
)

@Composable
fun StisbanksoalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color dimatikan dulu agar warna STIS kita muncul (bukan warna wallpaper HP)
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Mengubah warna Status Bar menjadi Biru STIS
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}