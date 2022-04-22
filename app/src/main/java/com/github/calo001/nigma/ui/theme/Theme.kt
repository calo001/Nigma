package com.github.calo001.nigma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.NigmaSalmon,
    primaryVariant = Color.NigmaSalmon,
    secondary = Color.NigmaAccent,
    secondaryVariant = Color.NigmaVariant,
    background = Color.NigmaSalmon,
    surface = Color.NigmaSalmonSurface,
    onSurface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Color.NigmaAccent,
    primaryVariant = Color.NigmaSalmon,
    secondary = Color.NigmaAccent,
    secondaryVariant = Color.NigmaVariant,
    background = Color.NigmaSalmon,
    surface = Color.NigmaSalmonSurface,
    onSurface = Color.Black,
)

@Composable
fun NigmaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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
}