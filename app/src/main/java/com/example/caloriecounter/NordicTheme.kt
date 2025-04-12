package com.example.caloriecounter

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun NordicTheme(content: @Composable () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val nordicColors = if (isDarkTheme) {
        darkColorScheme(
            primary = Color(0xFF4A6A8A),
            onPrimary = Color(0xFFD8DEE9),
            secondary = Color(0xFF6A4A4A),
            onSecondary = Color(0xFFA3BE8C),
            background = Color(0xFF2E3440),
            surface = Color(0xFF3B4252),
            error = Color(0xFFBF616A)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF2E4A5E),
            onPrimary = Color(0xFFECEFF1),
            secondary = Color(0xFF4A2E2E),
            onSecondary = Color(0xFFB0BEC5),
            background = Color(0xFFF5F7FA),
            surface = Color(0xFFECEFF1),
            error = Color(0xFFB71C1C)
        )
    }

    val runicTypography = Typography(
        headlineMedium = TextStyle(
            fontFamily = FontFamily(Font(R.font.runic_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = nordicColors.primary
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily(Font(R.font.norse_font)),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = nordicColors.onBackground
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily(Font(R.font.norse_font)),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = nordicColors.onSecondary
        )
    )

    MaterialTheme(
        colorScheme = nordicColors,
        typography = runicTypography,
        content = content
    )
}