package io.appkitchen.ante.core.designsystem.component

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class AvatarMemberTest {

    @Test
    fun initials_firstAndLastWord() {
        assertEquals("AR", member("Alex Rivera").initials(Locale.US))
        assertEquals("AR", member("Alex de la Rosa").initials(Locale.US))
        assertEquals("AR", member("  Alex   Rivera  ").initials(Locale.US))
    }

    @Test
    fun initials_singleWordGivesOne() {
        assertEquals("S", member("Sam").initials(Locale.US))
        assertEquals("", member("   ").initials(Locale.US))
    }

    @Test
    fun initials_uppercaseIsLocaleAware() {
        assertEquals("İK", member("ismail kaya").initials(Locale.forLanguageTag("tr-TR")))
        assertEquals("IK", member("ismail kaya").initials(Locale.US))
    }

    @Test
    fun initials_takeWholeGraphemes() {
        // A regional-indicator flag is two code points, four UTF-16 units; half of it is garbage.
        assertEquals("🇯🇵L", member("🇯🇵 Lee").initials(Locale.US))
        // Combining acute stays attached to its base.
        assertEquals("E\u0301", member("e\u0301mile").initials(Locale.US))
    }

    private fun member(name: String) = AvatarMember(id = "id", name = name)
}
