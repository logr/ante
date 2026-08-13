package io.appkitchen.ante.core.model

/** Account is a member and their balance. */
data class Account(
    val memberId: String,
    val balance: Money = Money.ZERO,
) {
    operator fun plus(money: Money): Account {
        return copy(balance = balance + money)
    }

    operator fun minus(money: Money): Account {
        return copy(balance = balance - money)
    }
}
