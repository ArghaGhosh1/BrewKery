package com.example.brewkery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.BrewkeryRepository
import com.example.brewkery.data.BrewkeryResponse
import com.example.brewkery.data.CartRepository
import kotlinx.coroutines.launch

sealed interface HomeState {
    data object Loading : HomeState
    data class Error(val message: String) : HomeState
    data class Success(val data: BrewkeryResponse) : HomeState
}

class HomeViewModel : ViewModel() {

    var state by mutableStateOf<HomeState>(HomeState.Loading)
        private set

    var query by mutableStateOf("")
    var selectedCategory by mutableStateOf<String?>(null) // null = All Items

    // Placeholders for the cart bar (cart logic comes later)

    init { load() }

    fun load() {
        state = HomeState.Loading
        viewModelScope.launch {
            BrewkeryRepository.load()
                .onSuccess {
                    CartRepository.setConfig(it.meta)
                    state = HomeState.Success(it)
                }
                .onFailure { state = HomeState.Error(it.message ?: "Something went wrong") }
        }
    }
}