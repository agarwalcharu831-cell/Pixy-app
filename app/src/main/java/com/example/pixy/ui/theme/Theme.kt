package com.example.pixy.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.pixy.model.ThemeMode

private val PixyDarkColorScheme = darkColorScheme(
    primary = PixyGreen,
    onPrimary = BgDark,
    secondary = PixyOrange,
    onSecondary = BgDark,
    tertiary = PixyPurple,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = Surface2Dark,
    onSurfaceVariant = MutedDark,
    outline = BorderDark,
    error = PixyOrange
)

private val PixyLightColorScheme = lightColorScheme(
    primary = PixyGreenDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PixyOrange,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = PixyPurple,
    background = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
    onBackground = androidx.compose.ui.graphics.Color(0xFF111827),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSurface = androidx.compose.ui.graphics.Color(0xFF111827),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF475569),
    outline = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    error = PixyOrange
)

@Composable
fun PixyTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    val rawScheme = if (useDark) PixyDarkColorScheme else PixyLightColorScheme

    val animatedBackground by animateColorAsState(rawScheme.background, label = "bg")
    val animatedSurface by animateColorAsState(rawScheme.surface, label = "surface")

    val colorScheme = rawScheme.copy(
        background = animatedBackground,
        surface = animatedSurface
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}