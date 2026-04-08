package com.example.qrphoneandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val QRColorScheme = lightColorScheme(
    primary = QRDeepBlue,
    secondary = QRLimeGreen,
    tertiary = QRCyan,
    background = QRWhite,
    surface = QRWhite,
    onPrimary = QRWhite,
    onSecondary = QRDarkText,
    onBackground = QRDarkText,
    onSurface = QRDarkText,
    error = QRError,
    onError = QRWhite,
)

@Composable
fun QRPhoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = QRColorScheme,
        content = content
    )
}
