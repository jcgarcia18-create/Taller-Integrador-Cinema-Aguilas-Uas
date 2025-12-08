package com.example.tallerintegrador.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta para el tema oscuro (original)
private val DarkColorScheme = darkColorScheme(
    primary = Yellow, // Amarillo brillante para modo oscuro
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = DarkBlue,
    onBackground = White,
    onSurface = White,
    secondary = Yellow.copy(alpha = 0.8f),
    tertiary = Yellow.copy(alpha = 0.6f),
    surfaceVariant = DarkBlue.copy(alpha = 0.8f),
    onSurfaceVariant = White.copy(alpha = 0.8f)
)

// Paleta para el tema claro (MEJORADA CON MEJOR CONTRASTE)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D47A1), // Azul oscuro intenso (excelente contraste)
    background = Color(0xFFF5F5F5), // Fondo gris muy claro
    surface = Color.White,
    onPrimary = Color.White, // Texto blanco sobre azul oscuro
    onBackground = Color(0xFF1C1B1F), // Texto muy oscuro sobre fondo claro
    onSurface = Color(0xFF1C1B1F), // Texto muy oscuro sobre surface
    secondary = Color(0xFF1565C0), // Azul medio
    tertiary = Color(0xFF0D47A1).copy(alpha = 0.7f),
    surfaceVariant = Color(0xFFE8EAF6), // Azul muy claro para cards
    onSurfaceVariant = Color(0xFF1C1B1F).copy(alpha = 0.8f),
    // Colores adicionales para mejor contraste
    primaryContainer = Color(0xFF1976D2).copy(alpha = 0.15f),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondaryContainer = Color(0xFFBBDEFB),
    onSecondaryContainer = Color(0xFF0D47A1)
)

@Composable
fun TallerIntegradorTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Crear o recordar el ThemeManager
    val themeManager = remember {
        ThemeManager(context)
    }

    // REACTIVO: Se actualiza cuando cambia el tema
    val isDarkMode by themeManager.isDarkMode.collectAsState()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkMode -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Iconos de la barra cambian según el tema
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }

    // Proveer el ThemeManager a toda la app
    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}