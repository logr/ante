package io.appkitchen.ante.core.model

import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {

    @Test
    fun `negation of money follows long negation`() {
        assertEquals(expected = Money(Long.MAX_VALUE), actual = -Money(Long.MIN_VALUE + 1))
        assertEquals(Money(Long.MIN_VALUE + 1), -Money(Long.MAX_VALUE))
    }

    @Test
    fun `abs() gives the absolute value of minor units`() {
        assertEquals(Money(Long.MIN_VALUE + 1).abs(), Money(Long.MAX_VALUE))
        assertEquals(Money(Long.MAX_VALUE).abs(), Money(Long.MAX_VALUE))
    }

    @Test
    fun `given MIN_VALUE, Money fails to create`() {
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MIN_VALUE)
        }
    }

    @Test
    fun `given a money value, can add another money value`() {
        val money = Money(9999)
        val one = Money(1)
        assertEquals(Money(10_000), money + one)
    }

    @Test
    fun `given a money value, can subtract another money value`() {
        val money = Money(0)
        val one = Money(1)
        assertEquals(expected = Money(-1), actual = money - one)
    }

    @Test
    fun `given a money value, can compare two values`() {
        val money = Money(10)
        val less = Money(9)
        val more = Money(11)

        assertEquals(expected = 1, actual = money.compareTo(less).sign)
        assertEquals(expected = -1, actual = money.compareTo(more).sign)
        assertEquals(expected = 0, actual = money.compareTo(money).sign)
    }

    @Test
    fun `given money, splitting into negative parts fails`() {
        val money = Money(10_000)
        assertFailsWith<IllegalArgumentException> {
            money.splitEqually(-1)
        }
    }

    @Test
    fun `given money, splitting into zero parts fails`() {
        val money = Money(10_000)
        assertFailsWith<IllegalArgumentException> {
            money.splitEqually(0)
        }
    }

    @Test
    fun `given a max money value, then adding 1 causes overflow exception`() {
        assertFailsWith<ArithmeticException> {
            Money(Long.MAX_VALUE) + Money(1)
        }
    }

    @Test
    fun `given a min + 1 money value, then subtracting 1 causes arithmetic exception`() {
        assertFailsWith<ArithmeticException> {
            Money(Long.MIN_VALUE + 1) - Money(1)
        }
    }

    @Test
    fun `given a min + 1 money value, then subtracting 2 causes arithmetic exception`() {
        assertFailsWith<ArithmeticException> {
            Money(Long.MIN_VALUE + 1) - Money(2)
        }
    }
}
