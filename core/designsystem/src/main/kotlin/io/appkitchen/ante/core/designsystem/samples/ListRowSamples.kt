package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteListRow
import io.appkitchen.ante.core.designsystem.component.Leading
import io.appkitchen.ante.core.designsystem.component.RowVariant
import io.appkitchen.ante.core.designsystem.component.SyncState
import io.appkitchen.ante.core.designsystem.component.Trailing
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

/**
 * Spec §3.3 screenshots: expense, settlement, voided, queued, failed, skeleton, light + dark;
 * Dense; one at 2x font scale showing the stacked label/amount case. 14 frames.
 *
 * Rows are full-bleed: they own their horizontal inset, and the settlement band and failed-sync
 * rule reach the frame's edges as they would the screen's. Copy follows the wireframes' row
 * pattern, "Dinner · Aug 12 · $42.00" = title + byline + trailing.
 */
val ListRowSample: ComponentSample =
    ComponentSample(
        id = "list_row",
        title = "AnteListRow",
        frames =
            listOf(
                SampleFrame("expense", fullBleed = true) {
                    AnteListRow(
                        title = "Dinner",
                        byline = "Aug 12 · Maya paid · split evenly",
                        leading = Leading.Avatar(SampleMembers[2]),
                        trailing = Trailing.Money("$42.00", MoneyTone.Neutral),
                        onClick = {},
                    )
                },
                SampleFrame("settlement", fullBleed = true) {
                    AnteListRow(
                        title = "Sam paid Alex",
                        variant = RowVariant.Settlement,
                        byline = "Aug 11",
                        trailing = Trailing.Money("$20.00", MoneyTone.Neutral),
                        onClick = {},
                    )
                },
                SampleFrame("voided", fullBleed = true) {
                    AnteListRow(
                        title = "Taxi",
                        byline = "Aug 10 · voided by Alex",
                        leading = Leading.Avatar(SampleMembers[0]),
                        trailing = Trailing.Money("$36.00", MoneyTone.Neutral),
                        onClick = {},
                        voided = true,
                    )
                },
                SampleFrame("queued", fullBleed = true) {
                    AnteListRow(
                        title = "Groceries",
                        byline = "Aug 12 · Sam paid",
                        leading = Leading.Avatar(SampleMembers[1]),
                        trailing = Trailing.Money("$57.25", MoneyTone.Neutral),
                        onClick = {},
                        syncState = SyncState.Queued,
                    )
                },
                SampleFrame("failed", fullBleed = true) {
                    AnteListRow(
                        title = "Concert tickets",
                        byline = "Aug 12 · Alex paid",
                        leading = Leading.Avatar(SampleMembers[0]),
                        trailing = Trailing.Money("$400.00", MoneyTone.Neutral),
                        onClick = {},
                        syncState = SyncState.Failed,
                    )
                },
                SampleFrame("skeleton", fullBleed = true) {
                    AnteListRow(title = "", variant = RowVariant.Skeleton)
                },
                SampleFrame("dense", themes = FrameThemes.LightOnly, fullBleed = true) {
                    AnteListRow(
                        title = "Weekend trip",
                        variant = RowVariant.Dense,
                        leading = Leading.Stack(SampleMembers.take(4)),
                        trailing = Trailing.Money("+$42.50", MoneyTone.Owed),
                        onClick = {},
                    )
                },
                SampleFrame(
                    "font_2x_stacked",
                    themes = FrameThemes.LightOnly,
                    fontScale = 2f,
                    fullBleed = true,
                ) {
                    AnteListRow(
                        title = "Alexandria Montgomery-Whitfield",
                        variant = RowVariant.Member,
                        byline = "owes you",
                        leading = Leading.Avatar(SampleMembers[3]),
                        trailing = Trailing.Money("−$1,204,517.30", MoneyTone.Owing),
                        onClick = {},
                    )
                },
            ),
    )
