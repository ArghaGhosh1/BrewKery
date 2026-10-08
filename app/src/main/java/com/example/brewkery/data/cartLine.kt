package com.example.brewkery.data

import java.util.UUID

data class CartLine(
    val key: String = UUID.randomUUID().toString(),
    val itemId: Int,
    val name: String,
    val imageUrl: String,
    val sizeLabel: String,
    val sugar: String,
    val milkLabel: String,
    val unitPrice: Double,
    val qty: Int
) {
    val total: Double get() = unitPrice * qty

    // Same item with the same options gets merged into one line
    fun sameAs(o: CartLine) = itemId == o.itemId && sizeLabel == o.sizeLabel &&
            sugar == o.sugar && milkLabel == o.milkLabel
}