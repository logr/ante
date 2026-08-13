package io.appkitchen.ante.core.model

import kotlin.math.sign

data class Transfer(
    val payerId: String,
    val payeeId: String,
    val amount: Money,
)

sealed interface SettlementResult {
    data class Ok(val transfers: List<Transfer>) : SettlementResult

    data object ErrBalancesNotZeroSum : SettlementResult

    data object ErrDuplicatedAccount : SettlementResult
}

/**
 * Takes a list of debts and returns, at most `n-1`, transfers that resolve all debts. Equal
 * balances in either direction are awarded by sorted member ID, so that transfers are deterministic
 * across same inputs. Sum of accounts must be equal to zero, otherwise returns a
 * [SettlementResult.ErrBalancesNotZeroSum]. If there is a duplicate account by member id then
 * [SettlementResult.ErrDuplicatedAccount] is returned.
 */
fun settleBalances(accounts: List<Account>): SettlementResult {
    // Ensure we have unique members
    val unique = accounts.distinctBy { it.memberId }
    if (unique.size != accounts.size) {
        return SettlementResult.ErrDuplicatedAccount
    }

    val payeeComparator = compareByDescending<Account> { it.balance }.thenBy { it.memberId }
    val payerComparator = compareBy<Account> { it.balance }.thenBy { it.memberId }

    val (payeeAccounts, payerAccounts) = splitAccountsByBalanceSign(accounts)

    payeeAccounts.sortWith(payeeComparator)
    payerAccounts.sortWith(payerComparator)

    if (isNotZeroSum(payeeAccounts, payerAccounts)) {
        return SettlementResult.ErrBalancesNotZeroSum
    }

    // Take largest and smallest from sorted list and determine the min absolute account value
    // between the two. Deduct, or add, that amount using sign-based opposite, from both accounts.
    // Add transfer of min amount from the debtor to the creditor. If either account has zero left,
    // remove from the list. Continue until the list is empty.
    val transfers = mutableListOf<Transfer>()
    while (payeeAccounts.isNotEmpty()) {
        val payerAccount = payerAccounts[0]
        val payeeAccount = payeeAccounts[0]

        val transferAmount = minOf(payerAccount.balance.abs(), payeeAccount.balance)
        transfers.add(
            Transfer(
                payerId = payerAccount.memberId,
                payeeId = payeeAccount.memberId,
                amount = transferAmount,
            )
        )

        payerAccounts[0] += transferAmount
        payeeAccounts[0] -= transferAmount

        if (payerAccounts[0].balance == Money.ZERO) {
            payerAccounts.removeAt(0)
        }
        if (payeeAccounts[0].balance == Money.ZERO) {
            payeeAccounts.removeAt(0)
        }
        payeeAccounts.sortWith(payeeComparator)
        payerAccounts.sortWith(payerComparator)
    }

    return SettlementResult.Ok(transfers)
}

private fun splitAccountsByBalanceSign(
    accounts: List<Account>
): Pair<MutableList<Account>, MutableList<Account>> {
    val nonZeroAccounts = accounts.filterNot { it.balance == Money.ZERO }
    val (payeeAccountsUnsorted, payerAccountsUnsorted) =
        nonZeroAccounts.partition { it.balance > Money.ZERO }
    val payeeAccounts = payeeAccountsUnsorted.toMutableList()
    val payerAccounts = payerAccountsUnsorted.toMutableList()
    return Pair(payeeAccounts, payerAccounts)
}

private fun isNotZeroSum(payeeAccounts: List<Account>, payerAccounts: List<Account>): Boolean {
    // Special sum that avoids overflow in valid zero-sum cases. If there is overflow
    // then the balances were never zero-sum.
    var balanceSum = 0L
    fun add(from: Account) {
        balanceSum = Math.addExact(balanceSum, from.balance.minorUnits)
    }
    try {
        var payeeIndex = 0
        var payerIndex = 0
        while (payeeIndex <= payeeAccounts.lastIndex || payerIndex <= payerAccounts.lastIndex) {
            // Here we decide which way to sum so that we ensure an overflow doesn't occur in
            // valid zero-sum cases.
            if (balanceSum.sign > 0) {
                // Check index is valid; if not, these Account balances are not zero-sum.
                if (payerIndex > payerAccounts.lastIndex) return true
                add(payerAccounts[payerIndex])
                payerIndex++
            } else {
                if (payeeIndex > payeeAccounts.lastIndex) return true
                add(payeeAccounts[payeeIndex])
                payeeIndex++
            }
        }
    } catch (_: ArithmeticException) {
        return true
    }
    return balanceSum != 0L
}
