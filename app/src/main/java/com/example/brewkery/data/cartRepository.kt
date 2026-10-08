package com.example.brewkery.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** In-memory cart shared by every screen. */
object CartRepository {

    var estimatedWait by mutableStateOf("")
        private set

    var lastOrder by mutableStateOf<Order?>(null)
        private set

    var lines by mutableStateOf<List<CartLine>>(emptyList())
        private set

    var symbol by mutableStateOf("$")
        private set

    var deliveryFee by mutableDoubleStateOf(0.0)
        private set

    var taxRatePercent by mutableDoubleStateOf(0.0)
        private set

    val itemCount: Int get() = lines.sumOf { it.qty }
    val subtotal: Double get() = lines.sumOf { it.total }
    val tax: Double get() = subtotal * taxRatePercent / 100.0
    val total: Double get() = if (lines.isEmpty()) 0.0 else subtotal + deliveryFee + tax

    fun setConfig(meta: Meta) {
        symbol = meta.currencySymbol
        deliveryFee = meta.deliveryFee
        taxRatePercent = meta.taxRatePercent
        estimatedWait = meta.estimatedDeliveryTime
    }

    fun add(line: CartLine) {
        val existing = lines.firstOrNull { it.sameAs(line) }
        lines = if (existing != null) {
            lines.map { if (it.key == existing.key) it.copy(qty = it.qty + line.qty) else it }
        } else {
            lines + line
        }
    }

    fun changeQty(key: String, delta: Int) {
        lines = lines.mapNotNull {
            if (it.key != key) it
            else it.copy(qty = it.qty + delta).takeIf { l -> l.qty > 0 }
        }
    }

    fun clear() { lines = emptyList() }

    fun placeOrder(): Order? {
        if (lines.isEmpty()) return null
        val order = Order(
            ticketId = "BK-${(10000..99999).random()}",
            itemCount = itemCount,
            estimatedWait = estimatedWait,
            etaMinutes = averageMinutes(estimatedWait),
            total = total
        )
        lastOrder = order
        lines = emptyList()
        return order
    }

    // "20 - 30 mins" -> 25
    private fun averageMinutes(text: String): Int? {
        val nums = Regex("\\d+").findAll(text).map { it.value.toInt() }.toList()
        return if (nums.isEmpty()) null else nums.average().toInt()
    }
}