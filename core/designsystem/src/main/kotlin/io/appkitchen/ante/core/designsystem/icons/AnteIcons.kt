/*
 * Icon path data in this file is exported from Material Symbols
 * (github.com/google/material-design-icons @ e083cc60a0828fdd3b404cea0cb8a5b900e9c23e,
 * symbols/web/<symbols_name>/materialsymbolsoutlined/<symbols_name>_24px.svg), Copyright Google
 * LLC, licensed under the Apache License, Version 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0. The full license text is in
 * LICENSE-material-design-icons alongside this file.
 *
 * Export axes, fixed: style Outlined, weight 400, fill 0, grade 0, optical size 24 - the
 * materialsymbolsoutlined 24px default assets. Symbols is a variable font: the same name exported
 * at other axis values is a different drawing, and golden comparison is exact, so any re-export
 * must restate these axes. Path data is each SVG's `d` attribute verbatim; the SVGs'
 * `viewBox="0 -960 960 960"` origin is reproduced by the translationY group in `symbolsIcon`.
 */

package io.appkitchen.ante.core.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * The app's icon set: exactly the twelve names the handoff spec (§5) lists, no more, vendored as
 * [ImageVector]s.
 *
 * Vendored rather than depended on: androidx's `material-icons-*` artifacts froze at 1.7.8 (the
 * Compose BOM pins them there while `ui` moves on) and carry the retired Material Icons drawings,
 * while Material Symbols - the current system - publishes no ImageVector artifact at all. Twelve
 * path strings cost nothing at build time, and bundling also pins the drawings: an upstream redraw
 * cannot silently churn goldens.
 *
 * Property KDoc records the Symbols name an icon was exported from where it differs from the spec's
 * Material Icons era name, so the mapping is reproducible. Call sites go through this object and
 * only ever see [ImageVector], so the source of any one icon can change without touching a caller.
 */
object AnteIcons {

    /** App-bar back. Auto-mirrors in RTL. Spec name: `arrow_back`. */
    val ArrowBack: ImageVector by lazy {
        symbolsIcon(
            name = "ArrowBack",
            autoMirror = true,
            pathData = "m313-440 224 224-57 56-320-320 320-320 57 56-224 224h487v80H313Z",
        )
    }

    /** Overflow menu. Spec name: `more_vert`. */
    val MoreVert: ImageVector by lazy {
        symbolsIcon(
            name = "MoreVert",
            pathData =
                "M480-160q-33 0-56.5-23.5T400-240q0-33 23.5-56.5T480-320q33 0 56.5 23.5T560-240q0" +
                    " 33-23.5 56.5T480-160Zm0-240q-33 0-56.5-23.5T400-480q0-33 23.5-56.5T480-560q33 0" +
                    " 56.5 23.5T560-480q0 33-23.5 56.5T480-400Zm0-240q-33 0-56.5-23.5T400-720q0-33 23" +
                    ".5-56.5T480-800q33 0 56.5 23.5T560-720q0 33-23.5 56.5T480-640Z",
        )
    }

    /** FAB. Spec name: `add`. */
    val Add: ImageVector by lazy {
        symbolsIcon(
            name = "Add",
            pathData = "M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z",
        )
    }

    /** Settlement row leading glyph. Spec name: `swap_horiz`. */
    val SwapHoriz: ImageVector by lazy {
        symbolsIcon(
            name = "SwapHoriz",
            pathData =
                "M280-160 80-360l200-200 56 57-103 103h287v80H233l103 103-56 57Zm400-240-56-57 10" +
                    "3-103H440v-80h287L624-743l56-57 200 200-200 200Z",
        )
    }

    /** Queued write. Spec name: `schedule`. */
    val Schedule: ImageVector by lazy {
        symbolsIcon(
            name = "Schedule",
            pathData =
                "m612-292 56-56-148-148v-184h-80v216l172 172ZM480-80q-83 0-156-31.5T197-197q-54-5" +
                    "4-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T763" +
                    "-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-400Z" +
                    "m0 320q133 0 226.5-93.5T800-480q0-133-93.5-226.5T480-800q-133 0-226.5 93.5T160-4" +
                    "80q0 133 93.5 226.5T480-160Z",
        )
    }

    /** Failed sync. Spec name: `sync_problem`. */
    val SyncProblem: ImageVector by lazy {
        symbolsIcon(
            name = "SyncProblem",
            pathData =
                "M120-160v-80h110l-16-14q-52-46-73-105t-21-119q0-111 66.5-197.5T360-790v84q-72 26" +
                    "-116 88.5T200-478q0 45 17 87.5t53 78.5l10 10v-98h80v240H120Zm360-120q-17 0-28.5-" +
                    "11.5T440-320q0-17 11.5-28.5T480-360q17 0 28.5 11.5T520-320q0 17-11.5 28.5T480-28" +
                    "0Zm-40-160v-240h80v240h-80Zm160 270v-84q72-26 116-88.5T760-482q0-45-17-87.5T690-" +
                    "648l-10-10v98h-80v-240h240v80H730l16 14q49 49 71.5 106.5T840-482q0 111-66.5 197." +
                    "5T600-170Z",
        )
    }

    /** Offline banner. Spec name: `cloud_off`. */
    val CloudOff: ImageVector by lazy {
        symbolsIcon(
            name = "CloudOff",
            pathData =
                "M792-56 686-160H260q-92 0-156-64T40-380q0-77 47.5-137T210-594q3-8 6-15.5t6-16.5L" +
                    "56-792l56-56 736 736-56 56ZM260-240h346L284-562q-2 11-3 21t-1 21h-20q-58 0-99 41" +
                    "t-41 99q0 58 41 99t99 41Zm185-161Zm419 191-58-56q17-14 25.5-32.5T840-340q0-42-29" +
                    "-71t-71-29h-60v-80q0-83-58.5-141.5T480-720q-27 0-52 6.5T380-693l-58-58q35-24 74." +
                    "5-36.5T480-800q117 0 198.5 81.5T760-520q69 8 114.5 59.5T920-340q0 39-15 72.5T864" +
                    "-210ZM593-479Z",
        )
    }

    /** Banner Info. Spec name: `info`. */
    val Info: ImageVector by lazy {
        symbolsIcon(
            name = "Info",
            pathData =
                "M440-280h80v-240h-80v240Zm40-320q17 0 28.5-11.5T520-640q0-17-11.5-28.5T480-680q-" +
                    "17 0-28.5 11.5T440-640q0 17 11.5 28.5T480-600Zm0 520q-83 0-156-31.5T197-197q-54-" +
                    "54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T76" +
                    "3-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-80q" +
                    "134 0 227-93t93-227q0-134-93-227t-227-93q-134 0-227 93t-93 227q0 134 93 227t227 " +
                    "93Zm0-320Z",
        )
    }

    /**
     * Banner Error and the error state block. Spec name: `error_outline`. Symbols name: `error`
     * (fill 0 has no separate `_outline` name).
     */
    val ErrorOutline: ImageVector by lazy {
        symbolsIcon(
            name = "ErrorOutline",
            pathData =
                "M480-280q17 0 28.5-11.5T520-320q0-17-11.5-28.5T480-360q-17 0-28.5 11.5T440-320q0" +
                    " 17 11.5 28.5T480-280Zm-40-160h80v-240h-80v240Zm40 360q-83 0-156-31.5T197-197q-5" +
                    "4-54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T" +
                    "763-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-8" +
                    "0q134 0 227-93t93-227q0-134-93-227t-227-93q-134 0-227 93t-93 227q0 134 93 227t22" +
                    "7 93Zm0-320Z",
        )
    }

    /** Join / add member. Spec name: `group_add`. */
    val GroupAdd: ImageVector by lazy {
        symbolsIcon(
            name = "GroupAdd",
            pathData =
                "M500-482q29-32 44.5-73t15.5-85q0-44-15.5-85T500-798q60 8 100 53t40 105q0 60-40 1" +
                    "05t-100 53Zm220 322v-120q0-36-16-68.5T662-406q51 18 94.5 46.5T800-280v120h-80Zm8" +
                    "0-280v-80h-80v-80h80v-80h80v80h80v80h-80v80h-80Zm-480-40q-66 0-113-47t-47-113q0-" +
                    "66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM0-160v-112q0-34 17.5-62" +
                    ".5T64-378q62-31 126-46.5T320-440q66 0 130 15.5T576-378q29 15 46.5 43.5T640-272v1" +
                    "12H0Zm320-400q33 0 56.5-23.5T400-640q0-33-23.5-56.5T320-720q-33 0-56.5 23.5T240-" +
                    "640q0 33 23.5 56.5T320-560ZM80-240h480v-32q0-11-5.5-20T540-306q-54-27-109-40.5T3" +
                    "20-360q-56 0-111 13.5T100-306q-9 5-14.5 14T80-272v32Zm240-400Zm0 400Z",
        )
    }

    /** Rename - the one mutable thing. Spec name: `edit`. */
    val Edit: ImageVector by lazy {
        symbolsIcon(
            name = "Edit",
            pathData =
                "M200-200h57l391-391-57-57-391 391v57Zm-80 80v-170l528-527q12-11 26.5-17t30.5-6q1" +
                    "6 0 31 6t26 18l55 56q12 11 17.5 26t5.5 30q0 16-5.5 30.5T817-647L290-120H120Zm640" +
                    "-584-56-56 56 56Zm-141 85-28-29 57 57-29-28Z",
        )
    }

    /** Detail links. Spec name: `chevron_right`. */
    val ChevronRight: ImageVector by lazy {
        symbolsIcon(
            name = "ChevronRight",
            pathData = "M504-480 320-664l56-56 240 240-240 240-56-56 184-184Z",
        )
    }

    private const val ICON_DP = 24
    private const val VIEWPORT = 960f

    private fun symbolsIcon(
        name: String,
        pathData: String,
        autoMirror: Boolean = false,
    ): ImageVector =
        ImageVector.Builder(
                name = "AnteIcons.$name",
                defaultWidth = ICON_DP.dp,
                defaultHeight = ICON_DP.dp,
                viewportWidth = VIEWPORT,
                viewportHeight = VIEWPORT,
                autoMirror = autoMirror,
            )
            .apply {
                // The Symbols viewBox is "0 -960 960 960"; shifting the group down by the
                // viewport height maps it onto the builder's 0..960 space with the data verbatim.
                addGroup(translationY = VIEWPORT)
                addPath(pathData = addPathNodes(pathData), fill = SolidColor(Color.Black))
            }
            .build()
}
