package io.appkitchen.ante.core.model

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MoneySplitPropertyTest {
    @Test
    fun `unary and abs() work as Long minor units`() = runTest {
        checkAll(Arb.moneyLong()) { a ->
            val m = Money(a)
            assertEquals(
                expected = m,
                actual = -(-m),
                message = "Double negation should yield original",
            )
            assertEquals(expected = Money.ZERO, actual = -m + m)
            assertTrue(m.abs() >= Money.ZERO, "Absolute value should be positive")
        }
    }

    @Test
    fun `split always preserves the total`() = runTest {
        checkAll(Arb.moneyLong(), Arb.int(1..1000)) { minorUnits, parts ->
            val splits = Money(minorUnits).splitEqually(parts)
            assertEquals(minorUnits, splits.sumOf { it.minorUnits })
        }
    }

    @Test
    fun `split produces exactly the requested number of parts`() = runTest {
        checkAll(Arb.moneyLong(), Arb.int(1..1000)) { minorUnits, parts ->
            assertEquals(parts, Money(minorUnits).splitEqually(parts).size)
        }
    }

    @Test
    fun `no two parts differ by more than one minor unit`() = runTest {
        checkAll(Arb.moneyLong(), Arb.int(1..1000)) { minorUnits, parts ->
            val units = Money(minorUnits).splitEqually(parts).map { it.minorUnits }
            assertTrue(units.max() - units.min() <= 1)
        }
    }
}
