package com.narimukkil.pocketai.ui.theme

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

private val CleanLightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlack,
    onPrimaryContainer = Color.White,
    
    secondary = TextSecondary,
    onSecondary = Color.White,
    
    background = BackgroundOffWhite,
    onBackground = TextPrimary,
    
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceWhite,
    onSurfaceVariant = TextSecondary,
    
    error = ExpenseRed,
    onError = Color.White
)

private val CleanDarkColorScheme = darkColorScheme(
    primary = PrimaryWhite,
    onPrimary = Color.Black,
    primaryContainer = PrimaryWhite,
    onPrimaryContainer = Color.Black,
    
    secondary = TextSecondaryDark,
    onSecondary = Color.Black,
    
    background = BackgroundPitchBlack,
    onBackground = TextPrimaryDark,
    
    surface = SurfaceDarkGray,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDarkGray,
    onSurfaceVariant = TextSecondaryDark,
    
    error = ExpenseRed,
    onError = Color.White
)

@Composable
fun PocketAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(), 
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CleanDarkColorScheme else CleanLightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
