package com.example.brewkery.navigation

sealed class Screen(var route: String) {

    object splashScreen: Screen("Splash_Screen")
    data object homeScreen: Screen("homeScreen")
    data object detailScreen : Screen("detailScreen/{id}") {
        fun createRoute(id: Int) = "detailScreen/$id"
    }

    data object cartScreen : Screen("cartScreen")
    data object orderPlacedScreen : Screen("orderPlacedScreen")
}