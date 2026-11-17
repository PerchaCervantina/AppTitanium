package com.titanium.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TitaniumColorScheme = darkColorScheme(
    primary = TitaniumRed,
    onPrimary = TitaniumOnDark,
    secondary = TitaniumSteel,
    onSecondary = TitaniumBlack,
    background = TitaniumBlack,
    onBackground = TitaniumOnDark,
    surface = TitaniumSurface,
    onSurface = TitaniumOnDark,
    surfaceVariant = TitaniumSurfaceVariant,
    onSurfaceVariant = TitaniumMuted,
    error = TitaniumRedDark
)

@Composable
fun TitaniumTheme(
    // La app siempre usa el tema oscuro, independientemente del ajuste del
    // sistema, para mantener la estética de la asociación.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = TitaniumColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TitaniumTypography,
        content = content
    )
}
