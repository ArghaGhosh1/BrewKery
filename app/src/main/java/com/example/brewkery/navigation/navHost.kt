package com.example.brewkery.navigation


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.brewkery.Presentation.CartScreen
import com.example.brewkery.Presentation.DetailScreen
import com.example.brewkery.Presentation.OrderPlacedScreen
import com.example.brewkery.ui.HomeScreen
import com.example.whatsappclone.presentation.spalashScreen.spalashScreen

@Composable
fun setUpNavHost(navController: NavHostController) {


    NavHost(startDestination = Screen.splashScreen.route, navController = navController) {

        composable(Screen.splashScreen.route) {

            spalashScreen(navController = navController)
        }

        composable(Screen.homeScreen.route) {

            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.detailScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            DetailScreen(navController = navController)
        }

        composable(route = Screen.cartScreen.route) {
            CartScreen(navController = navController)
        }

        composable(route = Screen.orderPlacedScreen.route) {
            OrderPlacedScreen(navController = navController)
        }


    }

}