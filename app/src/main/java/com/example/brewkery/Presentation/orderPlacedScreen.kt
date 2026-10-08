package com.example.brewkery.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.brewkery.data.CartRepository
import com.example.brewkery.navigation.Screen

// ---------- Colors (private to this file) ----------
private val Cream = Color(0xFFFFF8F4)
private val Peach = Color(0xFFFBE9E0)
private val Rust = Color(0xFFD94A26)
private val Ink = Color(0xFF150C0A)
private val Muted = Color(0xFF8A7F7A)
private val Line = Color(0xFFF0DDD2)
private val Green = Color(0xFF0B8A5B)
private val PillBg = Color(0xFFFFF3D6)
private val PillText = Color(0xFFD9A066)


@Composable
fun OrderPlacedScreen(navController: NavHostController) {
    val order = CartRepository.lastOrder

    val backToMenu: () -> Unit = {
        navController.popBackStack(Screen.homeScreen.route, inclusive = false)
    }

    Column(
        Modifier.fillMaxSize().background(Cream).statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Header
        Box(
            Modifier.size(92.dp).clip(CircleShape).background(Peach).border(1.5.dp, Rust, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text("☕", fontSize = 40.sp) }

        Spacer(Modifier.height(14.dp))
        Text(
            "ORDER DISPATCHED",
            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Rust, letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        Text("Brewing in Progress!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
        Spacer(Modifier.height(6.dp))
        Text(
            "Your ticket was dispatched to our barista.",
            fontSize = 12.sp, color = Muted, textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        // Ticket card
        val shape = RoundedCornerShape(16.dp)
        Column(
            Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ORDER TICKET", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Muted, letterSpacing = 0.5.sp)
                    Text(
                        "#" + (order?.ticketId ?: "BK-00000"),
                        fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace, color = Ink
                    )
                }
                Text(
                    "PREPARING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PillText,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(PillBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Divider()

            InfoRow(
                "Estimated Wait:",
                order?.estimatedWait?.replace("mins", "minutes").orEmpty().ifBlank { "--" },
                valueColor = Rust
            )
            InfoRow("Items Ordered:", "${order?.itemCount ?: 0} Item(s)", valueColor = Ink)

            Divider()

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Status:", fontSize = 10.sp, color = Muted)
                Text("Barista accepted your order!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green)
            }
        }

        Spacer(Modifier.weight(1.4f))

        // Back to menu
        Box(
            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                .background(Ink).clickable(onClick = backToMenu),
            contentAlignment = Alignment.Center
        ) {
            Text("Back to Menu", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Muted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
    }
}