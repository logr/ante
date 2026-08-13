package io.appkitchen.ante.core.model

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

fun Arb.Companion.moneyLong(): Arb<Long> {
    return Arb.long(Long.MIN_VALUE + 1..Long.MAX_VALUE)
}

fun Arb.Companion.money(): Arb<Money> {
    return Arb.moneyLong().map(::Money)
}

fun Arb.Companion.account(): Arb<Account> {
    return Arb.bind(Arb.string(), Arb.money(), ::Account)
}
