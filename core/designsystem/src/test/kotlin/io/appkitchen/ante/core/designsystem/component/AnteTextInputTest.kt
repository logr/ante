package io.appkitchen.ante.core.designsystem.component

import org.junit.Assert.assertEquals
import org.junit.Test

/** The digit-shift entry rules behind AnteCurrencyInput and AnteShareInput, without a renderer. */
class AnteTextInputTest {

    @Test
    fun formatMinor_alwaysTwoDecimals_grouped() {
        assertEquals("0.00", formatMinor(0))
        assertEquals("0.05", formatMinor(5))
        assertEquals("4.20", formatMinor(420))
        assertEquals("42.00", formatMinor(4200))
        assertEquals("1,204,517.30", formatMinor(120451730))
    }

    @Test
    fun minorFromDigits_shiftsInFromTheRight() {
        // "$0.00" then typing 4, 2, 0, 0 lands on $42.00.
        assertEquals(4L, minorFromDigits("0.004"))
        assertEquals(42L, minorFromDigits("0.042"))
        assertEquals(420L, minorFromDigits("0.420"))
        assertEquals(4200L, minorFromDigits("4.200"))
        // Backspace over the last digit shifts back out.
        assertEquals(420L, minorFromDigits("42.0"))
    }

    @Test
    fun minorFromDigits_ignoresEverythingButDigits_andCapsAtColumnReserve() {
        assertEquals(120450L, minorFromDigits("$1,204.50"))
        assertEquals(0L, minorFromDigits(""))
        assertEquals(0L, minorFromDigits("$."))
        // Seven integer digits plus two decimals is the reserve; a tenth digit is dropped.
        assertEquals(123456789L, minorFromDigits("1234567.891"))
    }

    @Test
    fun basisPoints_integerPercentUi() {
        assertEquals(2500, basisPointsFromDigits("25"))
        assertEquals(10000, basisPointsFromDigits("100"))
        assertEquals(10000, basisPointsFromDigits("1000"))
        assertEquals("25", formatBasisPoints(2500))
        assertEquals("12.50", formatBasisPoints(1250))
        assertEquals("0", formatBasisPoints(0))
    }
}
