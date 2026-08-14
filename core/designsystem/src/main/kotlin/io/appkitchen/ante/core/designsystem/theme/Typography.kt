package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.material3.Typography

/**
 * The Material 3 default type scale, unmodified, in Roboto - which is what the design project's
 * `tokens.json` (v2) specifies. `overriddenRoles` there is empty and nothing is bundled, so this is
 * the finished decision rather than a placeholder.
 *
 * Money styles are deliberately not here - see [AnteTextStyles]. Hijacking a Material role such as
 * `titleLarge` to carry tabular figures would change every non-money use of that role.
 */
internal val AnteTypography: Typography = Typography()
