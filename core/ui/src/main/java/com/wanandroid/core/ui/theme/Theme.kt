package com.wanandroid.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = WanBlue,
    onPrimary = Color.White,
    primaryContainer = WanBlueLight,
    secondary = WanOrange,
    background = WanLightGray,
    surface = Color.White,
    onBackground = WanTextPrimary,
    onSurface = WanTextPrimary,
    outline = WanDivider,
)

private val DarkColors = darkColorScheme(
    primary = WanBlueLight,
    onPrimary = DarkBlue,
    primaryContainer = WanBlueDark,
    secondary = WanOrange,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun WanAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = WanTypography,
        content = content,
    )
}
