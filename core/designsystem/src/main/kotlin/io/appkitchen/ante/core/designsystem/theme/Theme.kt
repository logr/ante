package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * The app's theme.
 *
 * Wraps [MaterialTheme] rather than replacing it. Material 3 components read `LocalColorScheme`,
 * `LocalTypography` and `LocalShapes` internally, so a theme that does not populate those renders
 * every Button and TextField in baseline purple no matter what tokens it defines. Anything Material
 * has no slot for - the spacing scale, money semantics, tabular money styles - rides alongside on
 * its own locals.
 *
 * Deliberately side effect free: no `SideEffect`, no reaching for the host Activity to tint system
 * bars. Those make the theme un-renderable outside an Activity, which would break the catalog and
 * any future screenshot test. Edge-to-edge stays in `:app` where it belongs.
 *
 * Dynamic color is not supported, and that is a decision rather than an omission: it would recolor
 * the roles the money semantics are built on, it cannot be screenshot-tested because it resolves
 * from the wallpaper at runtime, and it is API 31+ against a minSdk of 26.
 */
@Composable
fun AnteTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) AnteDarkColorScheme else AnteLightColorScheme
    val moneyColors = if (darkTheme) AnteDarkMoneyColors else AnteLightMoneyColors
    val avatarColors = if (darkTheme) AnteAvatarColors.Dark else AnteAvatarColors.Light

    CompositionLocalProvider(
        LocalAnteSpacing provides AnteSpacing.Default,
        LocalAnteMoneyColors provides moneyColors,
        LocalAnteAvatarColors provides avatarColors,
        LocalAnteTextStyles provides AnteTextStyles.Default,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AnteTypography,
            shapes = AnteShapes,
            content = content,
        )
    }
}

/**
 * Token accessor, so call sites read `AnteTheme.spacing.md` rather than reaching for the
 * CompositionLocals directly. Re-exposes the Material slots too, so components never need to
 * mention [MaterialTheme] by name.
 */
object AnteTheme {
    val spacing: AnteSpacing
        @Composable @ReadOnlyComposable get() = LocalAnteSpacing.current

    val money: AnteMoneyColors
        @Composable @ReadOnlyComposable get() = LocalAnteMoneyColors.current

    val avatarColors: AnteAvatarColors
        @Composable @ReadOnlyComposable get() = LocalAnteAvatarColors.current

    val textStyles: AnteTextStyles
        @Composable @ReadOnlyComposable get() = LocalAnteTextStyles.current

    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes
}
