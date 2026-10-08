package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.brewkery.HomeState
import com.example.brewkery.HomeViewModel
import com.example.brewkery.data.BrewkeryResponse
import com.example.brewkery.data.CartRepository
import com.example.brewkery.data.MenuItem
import com.example.brewkery.data.Meta
import com.example.brewkery.data.Order
import com.example.brewkery.navigation.Screen

// ---------- Colors ----------
private val Cream = Color(0xFFFFF8F4)
private val Peach = Color(0xFFF6E3D9)
private val Rust = Color(0xFFD94A26)
private val Ink = Color(0xFF1A1A1A)
private val Muted = Color(0xFF8A8A8A)
private val Line = Color(0xFFEBDDD5)
private val BadgeBg = Color(0xFFFFF0B3)
private val BadgeText = Color(0xFF7A5200)
private val Amber = Color(0xFFFFA000)
private val CartOrange = Color(0xFFFFA726)
private val Green = Color(0xFF0B8A5B)

private fun Double.money(symbol: String) = symbol + "%.2f".format(this)

// ---------- Screen ----------
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel(),
               navController : NavHostController) {
    Box(Modifier.fillMaxSize().background(Cream)) {
        when (val s = vm.state) {
            is HomeState.Loading -> CircularProgressIndicator(
                color = Rust, modifier = Modifier.align(Alignment.Center)
            )
            is HomeState.Error -> Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Couldn't load the menu", fontWeight = FontWeight.Bold, color = Ink)
                Text(s.message, color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                Button(onClick = vm::load, colors = ButtonDefaults.buttonColors(containerColor = Rust)) {
                    Text("Retry")
                }
            }
            is HomeState.Success -> HomeContent(s.data, vm, navController)        }
    }
}

@Composable
private fun BoxScope.HomeContent(
    data: BrewkeryResponse,
    vm: HomeViewModel,
    navController: NavHostController
) {
    val meta = data.meta
    val sym = meta.currencySymbol
    val order = CartRepository.lastOrder

    val visible = data.items.filter {
        (vm.selectedCategory == null || it.categoryId == vm.selectedCategory) &&
                (vm.query.isBlank() ||
                        it.name.contains(vm.query, ignoreCase = true) ||
                        it.tagline.contains(vm.query, ignoreCase = true))
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {

        HomeTopBar(
            cartCount = CartRepository.itemCount,
            onCartClick = { navController.navigate(Screen.cartScreen.route) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (order != null) {
                    ActiveOrderCard(order) { navController.navigate(Screen.orderPlacedScreen.route) }
                } else {
                    StoreInfoCard(meta)
                }
            }
            item { SearchBar(vm.query) { vm.query = it } }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CategoryChip("All Items", null, vm.selectedCategory == null) {
                            vm.selectedCategory = null
                        }
                    }
                    items(data.categories, key = { it.id }) { c ->
                        CategoryChip(c.name, c.icon, vm.selectedCategory == c.id) {
                            vm.selectedCategory = c.id
                        }
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        "No items match your search",
                        color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }
            items(visible, key = { it.id }) { item ->
                MenuItemCard(
                    item, sym,
                    onCustomize = { navController.navigate(Screen.detailScreen.createRoute(item.id)) }
                )
            }
        }
    }

    if (CartRepository.lines.isNotEmpty()) {
        CartBar(
            count = CartRepository.itemCount,
            total = CartRepository.subtotal.money(sym),
            onClick = { navController.navigate(Screen.cartScreen.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

// ---------- Top bar ----------
@Composable
private fun HomeTopBar(cartCount: Int, onCartClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(Ink),
            contentAlignment = Alignment.Center
        ) {
            Text("BK", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Fresh Roast & Bakes", fontSize = 10.sp, color = Muted)
            Text("Brewkery Artisans", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
        }

        Box(Modifier.size(46.dp)) {
            Box(
                Modifier.align(Alignment.Center).size(40.dp)
                    .clip(RoundedCornerShape(14.dp)).background(Color.White)
                    .border(1.dp, Line, RoundedCornerShape(14.dp))
                    .clickable(onClick = onCartClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ShoppingBag, "Cart", tint = Ink, modifier = Modifier.size(20.dp))
            }
            if (cartCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(Rust),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$cartCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ---------- Active order card ----------
@Composable
private fun ActiveOrderCard(order: Order, onTrack: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Peach).border(1.dp, Line, shape).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) { Text("☕", fontSize = 20.sp) }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ACTIVE ORDER", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(5.dp).clip(CircleShape).background(Rust))
            }
            Text(
                "Active Order #${order.ticketId}",
                fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Ink
            )
            Text(
                if (order.etaMinutes != null) "Preparing (Arriving in ${order.etaMinutes} mins)" else "Preparing",
                fontSize = 11.sp, color = Muted
            )
        }

        Text(
            "Track", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Green)
                .clickable(onClick = onTrack)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// ---------- Store info ----------
@Composable
private fun StoreInfoCard(meta: Meta) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Peach).border(1.dp, Line, shape).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Filled.TwoWheeler, null, tint = Rust, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("STORE INFO", color = Rust, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(5.dp).clip(CircleShape).background(Rust))
            }
            Text(
                "Delivery in " + meta.estimatedDeliveryTime.replace(" - ", " – "),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink
            )
            Text(meta.deliveryFee.money(meta.currencySymbol) + " flat fee", fontSize = 11.sp, color = Muted)
        }
        Text(
            "Open", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White)
                .padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

// ---------- Search ----------
@Composable
private fun SearchBar(value: String, onChange: (String) -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier.fillMaxWidth().height(46.dp).clip(shape).background(Color.White)
            .border(1.dp, Line, shape).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, null, tint = Ink, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) Text("Search roast, cold brew, pastry...", fontSize = 13.sp, color = Muted)
            BasicTextField(
                value = value, onValueChange = onChange, singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp, color = Ink),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ---------- Category chip ----------
@Composable
private fun CategoryChip(label: String, icon: String?, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        Modifier.clip(shape)
            .background(if (selected) Ink else Color.White)
            .border(1.dp, if (selected) Ink else Line, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!icon.isNullOrBlank()) {
            Text(icon, fontSize = 12.sp)
            Spacer(Modifier.width(6.dp))
        }
        Text(
            label, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Rust
        )
    }
}

// ---------- Menu card ----------
@Composable
private fun MenuItemCard(item: MenuItem, symbol: String, onCustomize: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)).background(Peach)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            if (item.badge.isNotBlank()) {
                Text(
                    item.badge, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BadgeText,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(BadgeBg)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Amber, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text("${item.rating} (${item.reviewCount})", fontSize = 11.sp, color = Muted)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.basePrice.money(symbol), fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace, color = Rust
                )
                Text(
                    "+ Customize", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Rust)
                        .clickable(onClick = onCustomize).padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ---------- Cart bar ----------
@Composable
private fun CartBar(count: Int, total: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Ink
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(26.dp).clip(CircleShape).background(Rust), contentAlignment = Alignment.Center) {
                Text("$count", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("View Your Cart", color = Color.White, fontSize = 11.sp)
                Text(total, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(1f))
            Text("Proceed to Checkout →", color = CartOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}