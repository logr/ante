package io.appkitchen.ante.core.model

import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * [Money] is a representation of money using [minorUnits] allowing for custom splitting that is
 * total-preserving. Adding or subtracting at the boundaries is checked and will throw an
 * [ArithmeticException]. Construction with [Long.MIN_VALUE] will throw an
 * [IllegalArgumentException].
 */
@JvmInline
value class Money(val minorUnits: Long) : Comparable<Money> {
    init {
        require(minorUnits != Long.MIN_VALUE) {
            "Invalid money amount: cannot be Long.MIN_VALUE"
        }
    }

    companion object {
        val ZERO = Money(0)
    }

    operator fun plus(other: Money): Money = checked(Math.addExact(minorUnits, other.minorUnits))

    operator fun minus(other: Money): Money =
        checked(Math.subtractExact(minorUnits, other.minorUnits))

    operator fun unaryMinus(): Money = Money(minorUnits = Math.negateExact(minorUnits))

    override operator fun compareTo(other: Money): Int = minorUnits.compareTo(other.minorUnits)

    fun abs(): Money = Money(minorUnits.absoluteValue)

    private fun checked(result: Long): Money {
        if (result == Long.MIN_VALUE) {
            throw ArithmeticException("Money overflow: result is Long.MIN_VALUE")
        }
        return Money(result)
    }

    // NOTE: no operator div. Division is deliberately absent -
    // callers must use io.appkitchen.ante.core.model.splitEqually(), which is total-preserving.
}

/**
 * Split `Money` equally into [parts] count of `Money` instances. The remainder is split and added
 * to the first `remainder` entries.
 */
fun Money.splitEqually(parts: Int): List<Money> {
    require(parts > 0) {
        "'parts' must be greater than 0"
    }
    val base = minorUnits / parts
    val remainder = (minorUnits % parts).toInt()
    // Each of the first |remainder| entries absorbs one extra minor unit in the remainder's
    // sign direction, so totals are preserved for negative amounts too.
    return List(parts) { i -> Money(base + if (i < remainder.absoluteValue) remainder.sign else 0) }
}
