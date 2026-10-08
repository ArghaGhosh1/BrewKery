package com.example.brewkery.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApi {
    @GET("VivekShah138/Brewkery/main/data.json")
    suspend fun getMenu(): BrewkeryResponse

    @GET("VivekShah138/Brewkery/main/api/items/{id}.json")
    suspend fun getItem(@Path("id") id: Int): ItemDetail
}

object BrewkeryRepository {
    private val json = Json { ignoreUnknownKeys = true }

    private val api: BrewkeryApi = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(BrewkeryApi::class.java)

    suspend fun load(): Result<BrewkeryResponse> = runCatching { api.getMenu() }

    suspend fun loadItem(id: Int): Result<ItemDetail> = runCatching { api.getItem(id) }

}