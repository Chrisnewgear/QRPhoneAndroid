package com.example.qrphoneandroid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = QRPrimary,
    onPrimary = QROnPrimary,
    primaryContainer = QRPrimaryContainer,
    onPrimaryContainer = QROnPrimaryContainer,
    secondary = QRSecondary,
    onSecondary = QROnSecondary,
    secondaryContainer = QRSecondaryContainer,
    onSecondaryContainer = QROnSecondaryContainer,
    tertiary = QRTertiary,
    onTertiary = QROnTertiary,
    tertiaryContainer = QRTertiaryContainer,
    onTertiaryContainer = QROnTertiaryContainer,
    error = QRError,
    onError = QROnError,
    errorContainer = QRErrorContainer,
    onErrorContainer = QROnErrorContainer,
    background = QRBackground,
    onBackground = QROnBackground,
    surface = QRSurface,
    onSurface = QROnSurface,
    surfaceVariant = QRSurfaceVariant,
    onSurfaceVariant = QROnSurfaceVariant,
    outline = QROutline,
    outlineVariant = QROutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = QRPrimaryDark,
    onPrimary = QROnPrimaryDark,
    primaryContainer = QRPrimaryContainerDark,
    onPrimaryContainer = QROnPrimaryContainerDark,
    secondary = QRSecondaryDark,
    onSecondary = QROnSecondaryDark,
    secondaryContainer = QRSecondaryContainerDark,
    onSecondaryContainer = QROnSecondaryContainerDark,
    tertiary = QRTertiaryDark,
    onTertiary = QROnTertiaryDark,
    tertiaryContainer = QRTertiaryContainerDark,
    onTertiaryContainer = QROnTertiaryContainerDark,
    error = QRErrorDark,
    onError = QROnErrorDark,
    errorContainer = QRErrorContainerDark,
    onErrorContainer = QROnErrorContainerDark,
    background = QRBackgroundDark,
    onBackground = QROnBackgroundDark,
    surface = QRSurfaceDark,
    onSurface = QROnSurfaceDark,
    surfaceVariant = QRSurfaceVariantDark,
    onSurfaceVariant = QROnSurfaceVariantDark,
    outline = QROutlineDark,
    outlineVariant = QROutlineVariantDark,
)


@Composable
fun QRPhoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode){
        SideEffect {
            val window = (view.context as Activity).window
            val insetController = WindowCompat.getInsetsController(window, view)

            insetController.isAppearanceLightStatusBars = !darkTheme
            insetController.isAppearanceLightNavigationBars = !darkTheme

        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
