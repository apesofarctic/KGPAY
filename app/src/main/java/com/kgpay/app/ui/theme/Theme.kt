package com.kgpay.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Brand palette — deep green (trust, money, "offline works too") + a warm accent for CTAs.
val KGPayGreen = Color(0xFF1B5E20)
val KGPayGreenDark = Color(0xFF0F3D13)
val KGPayGreenLight = Color(0xFFD9EFDA)
val KGPayAccent = Color(0xFFFF7A00)
val KGPaySuccess = Color(0xFF2E7D32)
val KGPayError = Color(0xFFB3261E)
val KGPayBackground = Color(0xFFF6F8F5)
val KGPaySurface = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = KGPayGreen,
    onPrimary = Color.White,
    primaryContainer = KGPayGreenLight,
    onPrimaryContainer = KGPayGreenDark,
    secondary = KGPayAccent,
    onSecondary = Color.White,
    background = KGPayBackground,
    onBackground = Color(0xFF1A1C19),
    surface = KGPaySurface,
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFE7ECE6),
    error = KGPayError,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FCB86),
    onPrimary = Color(0xFF0A3510),
    primaryContainer = KGPayGreenDark,
    onPrimaryContainer = KGPayGreenLight,
    secondary = Color(0xFFFFB877),
    onSecondary = Color(0xFF4A2800),
    background = Color(0xFF11150F),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF1A1F17),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF2A2F27),
    error = Color(0xFFFFB4AB),
)

private val KGPayTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp),
)

@Composable
fun KGPayTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = KGPayTypography,
        content = content,
    )
}
