package io.appkitchen.ante.core.model

import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.withAssumptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlinx.coroutines.test.runTest

class AccountTest {
    @Test
    fun `same account instances are not equal to other object types`() {
        val a = Account("", Money.ZERO)
        assertFalse(a.equals(10), "Account should not equal Int")
        assertFalse(a.equals(""), "Account should not equal String")
        assertFalse(a.equals(Money.ZERO), "Account should not equal Money")
    }

    @Test
    fun `same account instances are equal if member id and balance are equal`() = runTest {
        checkAll(Arb.account()) { a ->
            assertEquals(
                expected = a,
                actual = a,
                message = "Accounts should be equal by reference",
            )
        }
    }

    @Test
    fun `different account instances are equal if member id and balance are equal`() = runTest {
        checkAll(Arb.string(), Arb.money()) { id, amount ->
            val a = Account(id, amount)
            val b = Account(id, amount)
            assertEquals(
                expected = a,
                actual = b,
                message = "Accounts should be equal by value",
            )
        }
    }

    @Test
    fun `different account instances are not equal if member id and balance are not equal`() =
        runTest {
            checkAll(Arb.account(), Arb.account()) { a, b ->
                withAssumptions(a.memberId != b.memberId || a.balance != b.balance) {
                    assertNotEquals(a, b, "Should not be equal")
                }
            }
        }
}
