package com.appydinos.moviebrowser.ui.compose

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.toArgb

private val DarkColorPalette = darkColors(
    primary = CoralCoastTeal,
    primaryVariant = KingfisherBrightBlue,
    secondary = SpreadsheetGreen,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = CoralCoastTeal,
    primaryVariant = KingfisherBrightBlue,
    secondary = HedgeGardenGreen,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun MovieBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    windows: Window?,
    content: @Composable() () -> Unit
) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

    windows?.statusBarColor = colors.surface.toArgb()
    windows?.navigationBarColor = Transparent.toArgb()
}