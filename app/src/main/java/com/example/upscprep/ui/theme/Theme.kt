package com.example.upscprep.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.upscprep.utils.ThemeManager

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = Color.White,
    primaryContainer = MistBlue,
    onPrimaryContainer = DeepBlue,
    secondary = GoldAmber,
    onSecondary = Color.Black,
    tertiary = AccentTeal,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE3E8F6),
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    outlineVariant = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = MistBlue,
    onPrimary = Color.Black,
    primaryContainer = DeepBlue,
    onPrimaryContainer = Color.White,
    secondary = GoldAmber,
    onSecondary = Color.Black,
    tertiary = AccentTeal,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = ElevatedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark
)

@Composable
fun UPSCPrepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val savedTheme by ThemeManager.themeState()
        .collectAsState(initial = ThemeManager.getThemePreference(context))

    val prefersDark = when (savedTheme) {
        ThemeManager.ThemeMode.DARK -> true
        ThemeManager.ThemeMode.LIGHT -> false
        ThemeManager.ThemeMode.SYSTEM_DEFAULT -> darkTheme
    }

    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (prefersDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        prefersDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !prefersDark
        }
    }

    Crossfade(targetState = baseScheme, label = "theme_transitions") { scheme ->
        MaterialTheme(
            colorScheme = scheme,
            typography = UPSCTypography,
            shapes = UPSCShapes,
            content = content
        )
    }
}
