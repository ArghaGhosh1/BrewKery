package com.example.whatsappclone.presentation.spalashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.brewkery.R
import com.example.brewkery.navigation.Screen

import kotlinx.coroutines.delay

private val Cream = Color(0xFFFFF8F4)

@Composable
fun spalashScreen(
    navController: NavController
) {

    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate(Screen.homeScreen.route) {
            popUpTo(Screen.splashScreen.route) { inclusive = true }
        }
    }




    Box(modifier = Modifier.fillMaxSize()
        .background(color = Cream)) {

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
        )

    }
}