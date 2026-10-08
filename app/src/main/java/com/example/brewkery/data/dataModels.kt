package com.example.brewkery.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrewkeryResponse(
    val meta: Meta,
    val categories: List<Category>,
    val items: List<MenuItem>
)

@Serializable
data class Meta(
    val app: String,
    val tagline: String = "",
    @SerialName("currency_symbol") val currencySymbol: String = "$",
    @SerialName("delivery_fee") val deliveryFee: Double = 0.0,
    @SerialName("tax_rate_percent") val taxRatePercent: Double = 0.0,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: String = ""
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val icon: String = ""
)

@Serializable
data class MenuItem(
    val id: Int,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    val tagline: String = "",
    @SerialName("base_price") val basePrice: Double,
    val rating: Double = 0.0,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("image_url") val imageUrl: String = "",
    val badge: String = ""
)

@Serializable
data class ItemDetail(
    val id: Int,
    @SerialName("category_id") val categoryId: String = "",
    val name: String,
    val tagline: String = "",
    val description: String = "",
    @SerialName("base_price") val basePrice: Double,
    val rating: Double = 0.0,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("prep_time") val prepTime: String = "",
    val calories: Int = 0,
    @SerialName("image_url") val imageUrl: String = "",
    val badge: String = "",
    val ingredients: List<String> = emptyList(),
    val customizations: Customizations = Customizations()
)

@Serializable
data class Customizations(
    val sizes: List<SizeOption> = emptyList(),
    @SerialName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerialName("milk_options") val milkOptions: List<MilkOption> = emptyList()
)

@Serializable
data class SizeOption(
    val id: String,
    val label: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0
)

@Serializable
data class MilkOption(
    val id: String,
    val name: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0
)

