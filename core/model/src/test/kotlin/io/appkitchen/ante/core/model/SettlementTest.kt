package io.appkitchen.ante.core.model

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

// The max number of transfers we would want to test.
private const val MAX_TRANSFERS = 48

// The max amount for a single transfer. Ensures the overflow checks pass (e.g. `Math.addExact`)
// that are in place for `Money` values. Those checks will be tested explicitly in other tests.
private const val TRANSFER_BOUND = Long.MAX_VALUE / MAX_TRANSFERS

private data class TestTransfer(val from: Int, val to: Int, val amount: Long)

private fun Arb.Companion.memberIds(size: IntRange = 2..8): Arb<List<String>> {
    return Arb.int(size).map { n -> List(n) { i -> ('a' + i).toString() } }
}

private fun Arb.Companion.settleableAccounts(
    ids: Arb<List<String>> = Arb.memberIds(),
    amounts: Arb<Long> = Arb.long(1..TRANSFER_BOUND),
): Arb<List<Account>> {
    return ids.flatMap { members ->
        Arb.list(
                Arb.bind(
                    Arb.int(members.indices),
                    Arb.int(members.indices),
                    amounts,
                    ::TestTransfer,
                ),
                range = 0..MAX_TRANSFERS,
            )
            .map { transfers ->
                val balances = LongArray(members.size)
                for (t in transfers) {
                    if (t.from == t.to) continue
                    balances[t.from] -= t.amount
                    balances[t.to] += t.amount
                }
                members.mapIndexed { i, id -> Account(id, Money(balances[i])) }
            }
    }
}

private fun Arb.Companion.duplicatedAccounts(): Arb<List<Account>> =
    Arb.bind(Arb.settleableAccounts(), Arb.int(0..999), Arb.int(0..999)) { accts, pick, at ->
        val dup = accts[pick % accts.size].copy(balance = Money.ZERO)
        accts.toMutableList().apply { add(at % (size + 1), dup) }
    }

private fun Arb.Companion.unbalancedAccounts(): Arb<List<Account>> =
    Arb.bind(
        Arb.settleableAccounts(),
        Arb.int(0..999),
        Arb.long(1..TRANSFER_BOUND),
        Arb.boolean(),
    ) { accounts, pick, delta, negative ->
        val i = pick % accounts.size
        val d = if (negative) -delta else delta
        accounts.toMutableList().apply {
            this[i] = this[i].copy(balance = Money(this[i].balance.minorUnits + d))
        }
    }

class SettlementTest {
    @Test
    fun `when given sequenced values that would overflow but do sum to zero, still settle balances`() {
        val accounts =
            listOf(
                Account("a", Money(Long.MAX_VALUE)),
                Account("b", Money(Long.MAX_VALUE)),
                Account("c", Money(-Long.MAX_VALUE)),
                Account("d", Money(-Long.MAX_VALUE)),
            )
        val transfers = assertIs<SettlementResult.Ok>(settleBalances(accounts)).transfers
        assertContentEquals(
            expected =
                listOf(
                    Transfer("c", "a", Money(Long.MAX_VALUE)),
                    Transfer("d", "b", Money(Long.MAX_VALUE)),
                ),
            actual = transfers,
        )
    }

    @Test
    fun `when given sequenced values that would overflow but do not sum to zero, return unbalanced`() {
        val accounts =
            listOf(
                Account("a", Money(Long.MAX_VALUE)),
                Account("b", Money(Long.MAX_VALUE)),
                Account("c", Money(1)),
                Account("d", Money(-Long.MAX_VALUE)),
            )
        assertIs<SettlementResult.ErrBalancesNotZeroSum>(settleBalances(accounts))
    }

    @Test
    fun `when given equivalent balances the tie goes to member id sort order`() {
        val accounts =
            listOf(
                Account("d", Money(-5)),
                Account("c", Money(10)),
                Account("b", Money.ZERO),
                Account("a", Money(5)),
                Account("e", Money(-5)),
                Account("f", Money(-5)),
            )
        val transfers = assertIs<SettlementResult.Ok>(settleBalances(accounts)).transfers
        assertEquals(
            expected =
                listOf(
                    Transfer("d", "c", Money(5)),
                    Transfer("e", "a", Money(5)),
                    Transfer("f", "c", Money(5)),
                ),
            actual = transfers,
            message = "Transfers do not follow member id sort determinism",
        )
    }

    @Test
    fun `when given a duplicate member account, settleBalances returns a duplicate account error`() =
        runTest {
            checkAll(Arb.duplicatedAccounts()) { accounts ->
                val result = settleBalances(accounts)
                assertEquals(SettlementResult.ErrDuplicatedAccount, result)
            }
        }

    @Test
    fun `when given an unbalanced list of accounts, settleBalances returns an unbalanced error`() =
        runTest {
            checkAll(Arb.unbalancedAccounts()) { accounts ->
                val result = settleBalances(accounts)
                assertEquals(SettlementResult.ErrBalancesNotZeroSum, result)
            }
        }

    @Test
    fun `when given a set of settleable accounts, settleBalances produces a set of transfers that recreate the balances`() =
        runTest {
            checkAll(Arb.settleableAccounts()) { accounts ->
                val result = settleBalances(accounts)
                val transfers = assertIs<SettlementResult.Ok>(result).transfers
                val actualBalances = accounts.associate { it.memberId to Money.ZERO }.toMutableMap()
                for (transfer in transfers) {
                    actualBalances[transfer.payerId] =
                        actualBalances[transfer.payerId]!! - transfer.amount
                    actualBalances[transfer.payeeId] =
                        actualBalances[transfer.payeeId]!! + transfer.amount
                }
                val expectedBalances =
                    accounts.sortedBy { it.memberId }.map { it.memberId to it.balance }
                assertContentEquals(
                    expectedBalances,
                    actualBalances.toList().sortedBy { it.first },
                )
                assertTrue(transfers.size <= accounts.size - 1, "At most n-1 transfers")
            }
        }
}
