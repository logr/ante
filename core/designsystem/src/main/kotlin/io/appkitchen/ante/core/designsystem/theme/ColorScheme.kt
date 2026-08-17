package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * The brand palette, transcribed from the design project's `tokens.json` (v2).
 *
 * Every slot is listed even where the value matches the Material baseline. `lightColorScheme()`
 * silently defaults anything omitted, so a partial list compiles and runs while rendering part
 * brand and part baseline purple - a failure nothing catches until someone notices a stray surface.
 * Completeness here is what makes that impossible.
 *
 * Seeds, for regenerating rather than hand-editing: primary #4C5C92, secondary #5A5D72,
 * tertiary #74546E, neutral #919094, neutralVariant #8E909C, error #BA1A1A, standard contrast.
 */
internal val AnteLightColorScheme: ColorScheme =
    lightColorScheme(
        primary = Color(0xFF000000),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFDDE1FE),
        onPrimaryContainer = Color(0xFF001A41),
        inversePrimary = Color(0xFFB8C4FE),
        secondary = Color(0xFF5A5D72),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDEE1FA),
        onSecondaryContainer = Color(0xFF171B2C),
        tertiary = Color(0xFF74546E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFDD7F5),
        onTertiaryContainer = Color(0xFF2D1229),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD3),
        onErrorContainer = Color(0xFF390B00),
        background = Color(0xFFFAF9FD),
        onBackground = Color(0xFF1C1B1E),
        surface = Color(0xFFFAF9FD),
        onSurface = Color(0xFF1C1B1E),
        surfaceVariant = Color(0xFFE0E2EF),
        onSurfaceVariant = Color(0xFF444651),
        outline = Color(0xFF747682),
        outlineVariant = Color(0xFFC4C6D3),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF313033),
        inverseOnSurface = Color(0xFFF1F0F4),
        surfaceDim = Color(0xFFDAD9DE),
        surfaceBright = Color(0xFFFAF9FD),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF4F3F7),
        surfaceContainer = Color(0xFFEEEDF2),
        surfaceContainerHigh = Color(0xFFE9E7EC),
        surfaceContainerHighest = Color(0xFFE3E2E6),
        surfaceTint = Color(0xFF4C5C92),
        primaryFixed = Color(0xFFDDE1FE),
        onPrimaryFixed = Color(0xFF001A41),
        primaryFixedDim = Color(0xFFB8C4FE),
        onPrimaryFixedVariant = Color(0xFF324578),
        secondaryFixed = Color(0xFFDEE1FA),
        onSecondaryFixed = Color(0xFF171B2C),
        secondaryFixedDim = Color(0xFFC2C5DD),
        onSecondaryFixedVariant = Color(0xFF42465A),
        tertiaryFixed = Color(0xFFFDD7F5),
        onTertiaryFixed = Color(0xFF2D1229),
        tertiaryFixedDim = Color(0xFFE0BCD9),
        onTertiaryFixedVariant = Color(0xFF5B3D56),
    )

internal val AnteDarkColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFB8C4FE),
        onPrimary = Color(0xFF162F5F),
        primaryContainer = Color(0xFF324578),
        onPrimaryContainer = Color(0xFFDDE1FE),
        inversePrimary = Color(0xFF4C5C92),
        secondary = Color(0xFFC2C5DD),
        onSecondary = Color(0xFF2C2F42),
        secondaryContainer = Color(0xFF42465A),
        onSecondaryContainer = Color(0xFFDEE1FA),
        tertiary = Color(0xFFE0BCD9),
        onTertiary = Color(0xFF43273F),
        tertiaryContainer = Color(0xFF5B3D56),
        onTertiaryContainer = Color(0xFFFDD7F5),
        error = Color(0xFFFEB5A6),
        onError = Color(0xFF690001),
        errorContainer = Color(0xFF93010D),
        onErrorContainer = Color(0xFFFFDAD3),
        background = Color(0xFF141316),
        onBackground = Color(0xFFE3E2E6),
        surface = Color(0xFF141316),
        onSurface = Color(0xFFE3E2E6),
        surfaceVariant = Color(0xFF444651),
        onSurfaceVariant = Color(0xFFC4C6D3),
        outline = Color(0xFF8E909C),
        outlineVariant = Color(0xFF444651),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE3E2E6),
        inverseOnSurface = Color(0xFF313033),
        surfaceDim = Color(0xFF141316),
        surfaceBright = Color(0xFF39393C),
        surfaceContainerLowest = Color(0xFF0F0E12),
        surfaceContainerLow = Color(0xFF1C1B1E),
        surfaceContainer = Color(0xFF201F22),
        surfaceContainerHigh = Color(0xFF2A292D),
        surfaceContainerHighest = Color(0xFF353438),
        surfaceTint = Color(0xFFB8C4FE),
        primaryFixed = Color(0xFFDDE1FE),
        onPrimaryFixed = Color(0xFF001A41),
        primaryFixedDim = Color(0xFFB8C4FE),
        onPrimaryFixedVariant = Color(0xFF324578),
        secondaryFixed = Color(0xFFDEE1FA),
        onSecondaryFixed = Color(0xFF171B2C),
        secondaryFixedDim = Color(0xFFC2C5DD),
        onSecondaryFixedVariant = Color(0xFF42465A),
        tertiaryFixed = Color(0xFFFDD7F5),
        onTertiaryFixed = Color(0xFF2D1229),
        tertiaryFixedDim = Color(0xFFE0BCD9),
        onTertiaryFixedVariant = Color(0xFF5B3D56),
    )
