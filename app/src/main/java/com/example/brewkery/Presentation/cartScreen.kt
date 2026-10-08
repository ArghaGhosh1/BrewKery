package com.example.brewkery.Presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.brewkery.data.CartLine
import com.example.brewkery.data.CartRepository
import com.example.brewkery.navigation.Screen

// ---------- Colors / helpers (private to this file) ----------
private val Cream = Color(0xFFFFF8F4)
private val Peach = Color(0xFFFBE9E0)
private val Rust = Color(0xFFD94A26)
private val DarkRust = Color(0xFF9E2F12)
private val Ink = Color(0xFF1A1A1A)
private val Muted = Color(0xFF8A7F7A)
private val Line = Color(0xFFF0DDD2)
private val ClearRed = Color(0xFFE5365A)

private fun Double.money(symbol: String) = symbol + "%.2f".format(this)

// ---------- Screen ----------
@Composable
fun CartScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lines = CartRepository.lines
    val sym = CartRepository.symbol
    val isEmpty = lines.isEmpty()
    val total = CartRepository.total.money(sym)

    Column(Modifier.fillMaxSize().background(Cream).statusBarsPadding()) {

        // Top bar
        Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Box(
                Modifier.align(Alignment.CenterStart).size(36.dp).clip(CircleShape)
                    .background(Color.White).border(1.dp, Line, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink, modifier = Modifier.size(18.dp)) }

            Text(
                "YOUR CART", Modifier.align(Alignment.Center),
                fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Ink, letterSpacing = 0.5.sp
            )

            Text(
                "Clear Cart",
                Modifier.align(Alignment.CenterEnd).clickable { CartRepository.clear() },
                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClearRed
            )
        }

        // Items
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isEmpty) {
                item { EmptyCartCard() }
            } else {
                items(lines, key = { it.key }) { line -> CartRow(line, sym) }
            }
        }

        // Summary + Place order
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding()
                .padding(horizontal = 16.dp).padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SummaryCard(sym)

            Box(
                Modifier.fillMaxWidth().height(52.dp)
                    .alpha(if (isEmpty) 0.5f else 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Rust, DarkRust)))
                    .clickable(enabled = !isEmpty) {
                        CartRepository.placeOrder()
                        navController.navigate(Screen.orderPlacedScreen.route) {
                            popUpTo(Screen.homeScreen.route)   // removes the cart screen from the back stack
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Place Order Now  •  $total",
                        color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ---------- Empty state ----------
@Composable
private fun EmptyCartCard() {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape)
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Filled.ShoppingCart, null, tint = Color(0xFFEBCFC0), modifier = Modifier.size(34.dp))
        Text("Your in-memory cart is empty.", fontSize = 12.sp, color = Muted, textAlign = TextAlign.Center)
    }
}

// ---------- Cart row ----------
@Composable
private fun CartRow(line: CartLine, symbol: String) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(line.name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
            Text(
                listOf(line.sizeLabel, line.milkLabel).filter { it.isNotBlank() }.joinToString(" • "),
                fontSize = 10.sp, color = Muted
            )
            Text(
                line.total.money(symbol), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace, color = Rust
            )
        }
        MiniStepper(
            qty = line.qty,
            onMinus = { CartRepository.changeQty(line.key, -1) },
            onPlus = { CartRepository.changeQty(line.key, +1) }
        )
    }
}

@Composable
private fun MiniStepper(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        Modifier.clip(shape).background(Peach).border(1.dp, Line, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepBtn("−", onMinus)
        Text(
            "$qty", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace,
            color = Ink, textAlign = TextAlign.Center, modifier = Modifier.widthIn(min = 16.dp)
        )
        StepBtn("+", onPlus)
    }
}

@Composable
private fun StepBtn(label: String, onClick: () -> Unit) {
    Box(Modifier.size(width = 30.dp, height = 30.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
    }
}

// ---------- Summary ----------
@Composable
private fun SummaryCard(symbol: String) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        SummaryRow("Subtotal", CartRepository.subtotal.money(symbol))
        SummaryRow("Delivery Fee", CartRepository.deliveryFee.money(symbol))
        SummaryRow("Est. Tax (${"%.1f".format(CartRepository.taxRatePercent)}%)", CartRepository.tax.money(symbol))

        Box(
            Modifier.fillMaxWidth().height(1.dp).drawBehind {
                drawLine(
                    color = Color(0xFFE6D3C8),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            }
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Total Payable", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
            Text(CartRepository.total.money(symbol), fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Rust)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Muted)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Ink)
    }
}