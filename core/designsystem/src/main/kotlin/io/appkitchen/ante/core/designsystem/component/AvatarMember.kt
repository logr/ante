package io.appkitchen.ante.core.designsystem.component

import androidx.compose.runtime.Immutable
import java.text.BreakIterator
import java.util.Locale

/**
 * What an avatar needs to know about a member: a stable id to derive color from and a name to
 * derive initials from. Nothing else - never a payer, never a balance.
 *
 * Deliberately not `core:model`'s `Member`. The design system has no dependency on the domain, and
 * this is the design-system-local shape the spec calls `Member(id, name)`; the mapping from the
 * domain type happens in the caller.
 */
@Immutable data class AvatarMember(val id: String, val name: String)

/**
 * The initials rule from the handoff spec (§3.5): first grapheme of the first word plus first
 * grapheme of the last word, uppercased locale-aware; a single-word name gives one grapheme.
 *
 * Graphemes rather than chars so a name starting with an emoji or a combining sequence yields the
 * whole glyph rather than half a surrogate pair. Locale matters for uppercasing (Turkish dotless i)
 * and for the break iterator.
 */
internal fun AvatarMember.initials(locale: Locale): String {
    val words = name.trim().split(WHITESPACE).filter { it.isNotEmpty() }
    if (words.isEmpty()) return ""
    val first = words.first().firstGrapheme(locale)
    val last = if (words.size > 1) words.last().firstGrapheme(locale) else ""
    return (first + last).uppercase(locale)
}

private val WHITESPACE = Regex("\\s+")

private fun String.firstGrapheme(locale: Locale): String {
    val iterator = BreakIterator.getCharacterInstance(locale).also { it.setText(this) }
    val end = iterator.next()
    return if (end == BreakIterator.DONE) this else substring(0, end)
}
