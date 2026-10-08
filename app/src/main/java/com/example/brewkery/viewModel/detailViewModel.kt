package com.example.brewkery.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.BrewkeryRepository
import com.example.brewkery.data.ItemDetail
import kotlinx.coroutines.launch

sealed interface DetailState {
    data object Loading : DetailState
    data class Error(val message: String) : DetailState
    data class Success(val item: ItemDetail) : DetailState
}

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle["id"])

    var state by mutableStateOf<DetailState>(DetailState.Loading)
        private set

    var sizeIdx by mutableIntStateOf(0)
    var milkIdx by mutableIntStateOf(0)
    var sugarIdx by mutableIntStateOf(0)
    var qty by mutableIntStateOf(1)

    init { load() }

    fun load() {
        state = DetailState.Loading
        viewModelScope.launch {
            BrewkeryRepository.loadItem(itemId)
                .onSuccess { state = DetailState.Success(it) }
                .onFailure { state = DetailState.Error(it.message ?: "Something went wrong") }
        }
    }

    fun unitPrice(item: ItemDetail): Double {
        val c = item.customizations
        val size = c.sizes.getOrNull(sizeIdx)?.extraPrice ?: 0.0
        val milk = c.milkOptions.getOrNull(milkIdx)?.extraPrice ?: 0.0
        return item.basePrice + size + milk
    }

    fun totalPrice(item: ItemDetail) = unitPrice(item) * qty

    fun increment() { qty++ }
    fun decrement() { if (qty > 1) qty-- }
}