package com.example.brewkery.data

data class Order(
    val ticketId: String,
    val itemCount: Int,
    val estimatedWait: String,
    val etaMinutes: Int?,
    val total: Double
)