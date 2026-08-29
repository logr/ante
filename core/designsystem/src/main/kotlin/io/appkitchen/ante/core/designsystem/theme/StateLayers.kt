package io.appkitchen.ante.core.designsystem.theme

/**
 * State layer opacities from the design project's `tokens.json` (v2).
 *
 * The interaction alphas (pressed, focus, hover, dragged) are the Material 3 defaults, and
 * Material's own ripple applies them - components get pressed and focused treatment by building on
 * Material primitives, not by drawing a layer themselves. They are restated here so the conformance
 * test can hold Material's `RippleDefaults` to the sheet, and so a component that must draw a layer
 * by hand has a token to cite.
 *
 * The disabled alphas are the ones components read directly: Material applies them inside its own
 * disabled states, but voided rows, static tags and skeleton bars are not "disabled" in Material's
 * sense and need the same numbers.
 *
 * A plain object for the same reason as [AnteElevation]: nothing varies these by theme.
 */
object AnteStateLayers {
    const val HOVER = 0.08f
    const val FOCUS = 0.10f
    const val PRESSED = 0.10f
    const val DRAGGED = 0.16f
    const val DISABLED_CONTENT = 0.38f
    const val DISABLED_CONTAINER = 0.12f
}
