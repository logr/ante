package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.R
import io.appkitchen.ante.core.designsystem.icons.AnteIcons
import io.appkitchen.ante.core.designsystem.theme.AnteStateLayers
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

enum class RowVariant {
    Expense,
    /**
     * A full-width surfaceContainerLow band with a swap_horiz glyph. A fact, not a state machine.
     */
    Settlement,
    Group,
    Member,
    /** One line, 48dp. */
    Dense,
    /** Ghost bars at final row geometry, non-interactive. This is the row's loading state. */
    Skeleton,
}

/** Whether the row's write has reached the server. Never changes an amount's tone. */
enum class SyncState {
    Synced,
    Queued,
    Failed,
}

/** What sits at the row's start. */
sealed interface Leading {
    data class Avatar(val member: AvatarMember) : Leading

    data class Stack(val members: List<AvatarMember>) : Leading

    data class Icon(val vector: ImageVector) : Leading
}

/** What sits at the row's end. */
sealed interface Trailing {
    /** [text] is the formatter's output, prefix included - see [AnteMoneyText]. */
    data class Money(val text: String, val tone: MoneyTone) : Trailing

    data class Text(val value: String) : Trailing

    data class Icon(val vector: ImageVector) : Trailing
}

/**
 * The list row (spec §3.3): expenses, settlements, groups, members, and their loading skeleton.
 *
 * One click target - the whole row, merged - with the leading and trailing never separately
 * clickable. Disabled, loading and error do not exist as states: a row without [onClick] is simply
 * inert and undimmed, loading is the [RowVariant.Skeleton] variant, and errors are [syncState].
 * Nothing swipes; nothing is deletable.
 *
 * [voided] dims every piece of content to the disabled-content alpha, strikes the title and the
 * amount, and appends a static "Voided" chip - and the row stays in the ledger and stays tappable.
 * [SyncState.Queued] appends "· queued" plus a schedule glyph in the pending color without touching
 * the amount's tone; [SyncState.Failed] draws the 3dp error rule at the leading edge - which means
 * failed-sync and nothing else, app-wide - and replaces the byline with the retry copy.
 *
 * At 2x font scale and above the byline may wrap to two lines and the amount stacks under the title
 * rather than shrinking. Money never truncates: the title column yields first.
 */
@Composable
fun AnteListRow(
    title: String,
    modifier: Modifier = Modifier,
    variant: RowVariant = RowVariant.Expense,
    byline: String? = null,
    leading: Leading? = null,
    trailing: Trailing? = null,
    onClick: (() -> Unit)? = null,
    syncState: SyncState = SyncState.Synced,
    voided: Boolean = false,
) {
    if (variant == RowVariant.Skeleton) {
        SkeletonRow(modifier)
        return
    }

    val scheme = AnteTheme.colorScheme
    val container =
        when (variant) {
            RowVariant.Settlement -> scheme.surfaceContainerLow
            else -> scheme.surface
        }
    // A settlement always carries the swap glyph unless the caller says otherwise.
    val resolvedLeading =
        leading ?: if (variant == RowVariant.Settlement) Leading.Icon(AnteIcons.SwapHoriz) else null

    val voidedState = stringResource(R.string.ante_state_voided)
    val queuedState = stringResource(R.string.ante_state_not_synced)
    val stateModifier = Modifier.semantics {
        if (voided) stateDescription = voidedState
        if (syncState == SyncState.Queued) stateDescription = queuedState
        if (syncState == SyncState.Failed) liveRegion = LiveRegionMode.Polite
    }
    val minHeight =
        if (byline == null || variant == RowVariant.Dense) ONE_LINE_MIN_HEIGHT
        else TWO_LINE_MIN_HEIGHT
    val rowModifier =
        modifier
            .fillMaxWidth()
            .topRule(scheme.outlineVariant)
            .heightIn(min = minHeight)
            .then(stateModifier)

    val body: @Composable () -> Unit = {
        RowContent(
            title = title,
            byline = byline,
            leading = resolvedLeading,
            trailing = trailing,
            syncState = syncState,
            voided = voided,
        )
    }
    if (onClick != null) {
        Surface(
            onClick = onClick,
            color = container,
            modifier = rowModifier.semantics { role = Role.Button },
            content = body,
        )
    } else {
        Surface(
            color = container,
            modifier = rowModifier.semantics(mergeDescendants = true) {},
            content = body,
        )
    }
}

@Composable
private fun RowContent(
    title: String,
    byline: String?,
    leading: Leading?,
    trailing: Trailing?,
    syncState: SyncState,
    voided: Boolean,
) {
    val scheme = AnteTheme.colorScheme
    val spacing = AnteTheme.spacing
    val dimmed = scheme.onSurface.copy(alpha = AnteStateLayers.DISABLED_CONTENT)
    val titleColor = if (voided) dimmed else scheme.onSurface
    val bylineColor = if (voided) dimmed else scheme.onSurfaceVariant
    // The spec's stacked case: byline may wrap and the amount drops under the title.
    val stacked = LocalDensity.current.fontScale >= STACK_FONT_SCALE

    // Intrinsic height so the failed-sync rule can fill the row's full height.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        if (syncState == SyncState.Failed) {
            Box(Modifier.width(FAILED_RULE_WIDTH).fillMaxHeight().background(scheme.error))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.weight(1f)
                    .padding(
                        horizontal = spacing.screenHorizontal,
                        vertical = spacing.listRowVertical,
                    ),
        ) {
            if (leading != null) {
                Box(Modifier.alpha(if (voided) AnteStateLayers.DISABLED_CONTENT else 1f)) {
                    LeadingSlot(leading)
                }
                Spacer(Modifier.width(spacing.relatedGap))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = titleColor,
                    style = AnteTheme.typography.bodyLarge,
                    textDecoration = if (voided) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (stacked && trailing != null) {
                    TrailingSlot(trailing, voided)
                }
                Byline(
                    byline = byline,
                    color = bylineColor,
                    syncState = syncState,
                    voided = voided,
                    maxLines = if (stacked) 2 else 1,
                )
            }
            if (!stacked && trailing != null) {
                Spacer(Modifier.width(spacing.relatedGap))
                TrailingSlot(trailing, voided)
            }
        }
    }
}

@Composable
private fun LeadingSlot(leading: Leading) {
    when (leading) {
        is Leading.Avatar -> AnteAvatar(leading.member)
        is Leading.Stack -> AnteAvatarStack(leading.members)
        is Leading.Icon ->
            Icon(
                imageVector = leading.vector,
                contentDescription = null,
                tint = AnteTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(ICON_SIZE),
            )
    }
}

@Composable
private fun TrailingSlot(trailing: Trailing, voided: Boolean) {
    when (trailing) {
        is Trailing.Money ->
            AnteMoneyText(text = trailing.text, tone = trailing.tone, voided = voided)
        is Trailing.Text ->
            Text(
                text = trailing.value,
                color =
                    if (voided) {
                        AnteTheme.colorScheme.onSurface.copy(
                            alpha = AnteStateLayers.DISABLED_CONTENT
                        )
                    } else {
                        AnteTheme.colorScheme.onSurfaceVariant
                    },
                style = AnteTheme.typography.bodyMedium,
                maxLines = 1,
            )
        is Trailing.Icon ->
            Icon(
                imageVector = trailing.vector,
                contentDescription = null,
                tint = AnteTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier.size(ICON_SIZE)
                        .alpha(if (voided) AnteStateLayers.DISABLED_CONTENT else 1f),
            )
    }
}

/**
 * The byline and whatever the row's state appends to it: the "· queued" fragment, the "Voided" tag,
 * or - for a failed sync - the retry copy in place of the byline entirely.
 */
@Composable
private fun Byline(
    byline: String?,
    color: Color,
    syncState: SyncState,
    voided: Boolean,
    maxLines: Int,
) {
    val style = AnteTheme.typography.bodyMedium
    if (syncState == SyncState.Failed) {
        Text(
            text = stringResource(R.string.ante_row_failed_sync),
            color = AnteTheme.colorScheme.error,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
        return
    }
    if (byline == null && syncState == SyncState.Synced && !voided) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AnteTheme.spacing.inlineGap),
    ) {
        if (byline != null) {
            Text(
                text = byline,
                color = color,
                style = style,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        if (syncState == SyncState.Queued) {
            val pending = AnteTheme.money.forTone(MoneyTone.Pending).content
            Text(
                text = stringResource(R.string.ante_row_queued),
                color = pending,
                style = style,
                maxLines = 1,
            )
            Icon(
                imageVector = AnteIcons.Schedule,
                contentDescription = null,
                tint = pending,
                modifier = Modifier.size(SYNC_ICON_SIZE),
            )
        }
        if (voided) {
            AnteChip(label = stringResource(R.string.ante_row_voided), variant = ChipVariant.Static)
        }
    }
}

/**
 * Ghost bars at the geometry of a two-line row with a leading avatar and a trailing amount. Not
 * announced: a skeleton says nothing worth reading.
 */
@Composable
private fun SkeletonRow(modifier: Modifier) {
    val scheme = AnteTheme.colorScheme
    val spacing = AnteTheme.spacing
    val ghost = scheme.onSurface.copy(alpha = AnteStateLayers.DISABLED_CONTAINER)
    Surface(
        color = scheme.surface,
        modifier =
            modifier
                .fillMaxWidth()
                .topRule(scheme.outlineVariant)
                .heightIn(min = TWO_LINE_MIN_HEIGHT)
                .clearAndSetSemantics {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.listRowVertical,
                ),
        ) {
            Box(Modifier.size(AVATAR_SIZE).background(ghost, CircleShape))
            Spacer(Modifier.width(spacing.relatedGap))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                GhostBar(ghost, widthFraction = 0.55f, height = TITLE_BAR_HEIGHT)
                GhostBar(ghost, widthFraction = 0.35f, height = BYLINE_BAR_HEIGHT)
            }
            Spacer(Modifier.width(spacing.relatedGap))
            Box(Modifier.width(AMOUNT_BAR_WIDTH).height(TITLE_BAR_HEIGHT).background(ghost))
        }
    }
}

@Composable
private fun GhostBar(color: Color, widthFraction: Float, height: Dp) {
    Box(Modifier.fillMaxWidth(widthFraction).height(height).background(color))
}

/** The 1dp outlineVariant rule every row carries along its top edge. */
private fun Modifier.topRule(color: Color): Modifier = drawBehind {
    val stroke = 1.dp.toPx()
    drawLine(color, Offset(0f, stroke / 2), Offset(size.width, stroke / 2), stroke)
}

private val ONE_LINE_MIN_HEIGHT = 48.dp
private val TWO_LINE_MIN_HEIGHT = 64.dp
private val ICON_SIZE = 24.dp
private val SYNC_ICON_SIZE = 16.dp
private val AVATAR_SIZE = 32.dp
private val FAILED_RULE_WIDTH = 3.dp
private val TITLE_BAR_HEIGHT = 14.dp
private val BYLINE_BAR_HEIGHT = 10.dp
private val AMOUNT_BAR_WIDTH = 64.dp
private const val STACK_FONT_SCALE = 2f
