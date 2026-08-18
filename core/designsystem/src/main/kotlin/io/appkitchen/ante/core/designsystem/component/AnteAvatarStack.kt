package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.R
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

/**
 * Membership at a glance (spec §3.5): up to three initials avatars overlapping by 8dp, then a "+N"
 * tile for the rest. Represents membership only - never who paid.
 *
 * One semantics node for the whole stack, "4 members: Alex, Sam, Maya, Jordan"; the avatars are
 * never individual targets, and when [onClick] is set the whole stack is one 48dp button. The cap
 * of three is fixed, not a parameter. The overflow tile is a circle that widens into a pill for a
 * count that does not fit - "+99" is never clipped.
 *
 * Color and initials derivation are the spec's rules as code: see `AnteAvatarColors` and
 * [initials]. Keyed to the member id, so a rename never recolors.
 */
@Composable
fun AnteAvatarStack(
    members: List<AvatarMember>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val visible = members.take(MAX_VISIBLE)
    val overflow = members.size - visible.size
    val description =
        pluralStringResource(
            R.plurals.ante_avatar_stack_members,
            members.size,
            members.size,
            members.joinToString { it.name },
        )
    val interaction =
        if (onClick != null) {
            Modifier.minimumInteractiveComponentSize()
                .clickable(role = Role.Button, onClick = onClick)
        } else {
            Modifier
        }
    Row(
        horizontalArrangement = Arrangement.spacedBy(-OVERLAP),
        verticalAlignment = Alignment.CenterVertically,
        // Semantics are cleared after the click modifier so the click survives and only the
        // per-avatar text is replaced by the one description.
        modifier =
            modifier.then(interaction).clearAndSetSemantics { contentDescription = description },
    ) {
        visible.forEach { member -> AnteAvatar(member) }
        if (overflow > 0) {
            OverflowTile(count = overflow)
        }
    }
}

/**
 * One initials avatar. Shared with `AnteListRow`'s leading slot, which is why it is not private.
 *
 * Decorative to accessibility: the stack speaks the member list, and a row's title or byline
 * already names the person, so the initials are cleared rather than read out as letters.
 */
@Composable
internal fun AnteAvatar(member: AvatarMember, modifier: Modifier = Modifier) {
    val colors = AnteTheme.avatarColors.forMember(member.id)
    val locale = Locale.current.platformLocale
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(colors.container)
                .border(RING_WIDTH, AnteTheme.colorScheme.surface, CircleShape)
                .clearAndSetSemantics {},
    ) {
        Text(
            text = member.initials(locale),
            color = colors.content,
            style = AnteTheme.typography.labelSmall,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun OverflowTile(count: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.defaultMinSize(minWidth = AVATAR_SIZE, minHeight = AVATAR_SIZE)
                .clip(CircleShape)
                .background(AnteTheme.colorScheme.surfaceContainerHighest)
                .border(RING_WIDTH, AnteTheme.colorScheme.surface, CircleShape)
                .padding(horizontal = AnteTheme.spacing.inlineGap),
    ) {
        Text(
            text = "+$count",
            color = AnteTheme.colorScheme.onSurfaceVariant,
            style = AnteTheme.typography.labelSmall,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
        )
    }
}

private const val MAX_VISIBLE = 3
private val AVATAR_SIZE = 32.dp
private val OVERLAP = 8.dp
private val RING_WIDTH = 1.dp
