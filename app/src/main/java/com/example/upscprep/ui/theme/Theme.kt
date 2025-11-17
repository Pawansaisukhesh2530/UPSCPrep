package com.example.upscprep.ui.theme

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.upscprep.utils.ThemeHelper

private val DarkColorScheme = darkColorScheme(
    primary = GradientStart,
    secondary = GradientEnd,
    tertiary = AccentCoral,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun UPSCPrepTheme(
    // keep parameter for compatibility but actual theme is derived from saved pref
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Changed to false to use our custom theme
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Observe ThemeHelper's stateFlow so Compose recomposes when theme changes
    val savedTheme by ThemeHelper.themeStateFlow().collectAsState(initial = ThemeHelper.getSavedTheme(context))
    val darkThemeFinal = when (savedTheme) {
        ThemeHelper.THEME_DARK -> true
        ThemeHelper.THEME_LIGHT -> false
        ThemeHelper.THEME_SYSTEM -> isSystemInDarkTheme()
        else -> darkTheme // fallback to the provided parameter to avoid unused-parameter warning
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkThemeFinal) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkThemeFinal -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Avoid direct assignment to statusBarColor (deprecated); only control icon appearance
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkThemeFinal
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}