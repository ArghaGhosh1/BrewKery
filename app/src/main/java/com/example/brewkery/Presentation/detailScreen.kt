package com.example.brewkery.Presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.brewkery.data.CartLine
import com.example.brewkery.data.CartRepository
import com.example.brewkery.data.ItemDetail
import com.example.brewkery.navigation.Screen
import com.example.brewkery.viewModel.DetailState
import com.example.brewkery.viewModel.DetailViewModel

// ---------- Colors / helpers (private to this file) ----------
private val Cream = Color(0xFFFFF8F4)
private val Peach = Color(0xFFFBE9E0)
private val Rust = Color(0xFFD94A26)
private val DarkRust = Color(0xFF9E2F12)
private val Ink = Color(0xFF1A1A1A)
private val Muted = Color(0xFF7A7A7A)
private val Line = Color(0xFFEBDDD5)

private const val SYMBOL = "$" // item endpoint has no currency field
private fun Double.money() = SYMBOL + "%.2f".format(this)

// ---------- Screen ----------
@Composable
fun DetailScreen(vm: DetailViewModel = viewModel(),
                 navController : NavHostController
) {

    val onBack: () -> Unit = { navController.popBackStack()}

        Box(Modifier.fillMaxSize().background(Cream)) {
        when (val s = vm.state) {
            is DetailState.Loading -> {
                CircularProgressIndicator(color = Rust, modifier = Modifier.align(Alignment.Center))
                BackButton(onBack, Modifier.align(Alignment.TopStart).statusBarsPadding().padding(12.dp))
            }
            is DetailState.Error -> {
                Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Couldn't load this item", fontWeight = FontWeight.Bold, color = Ink)
                    Text(s.message, color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Button(onClick = vm::load, colors = ButtonDefaults.buttonColors(containerColor = Rust)) {
                        Text("Retry")
                    }
                }
                BackButton(onBack, Modifier.align(Alignment.TopStart).statusBarsPadding().padding(12.dp))
            }
            is DetailState.Success -> DetailContent(s.item, vm, onBack, navController)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    item: ItemDetail,
    vm: DetailViewModel,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val c = item.customizations
    var liked by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {

        // Separate top bar
        DetailTopBar(
            liked = liked,
            onBack = onBack,
            onToggleLike = { liked = !liked }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero image + badge tag
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                        .clip(RoundedCornerShape(20.dp)).background(Peach)
                )
                if (item.badge.isNotBlank()) {
                    Text(
                        item.badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFC107),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC1A1A1A))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Title + live unit price
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    item.name, Modifier.weight(1f).padding(end = 12.dp),
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Ink
                )
                Text(vm.unitPrice(item).money(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Rust)
            }

            Text(item.description, fontSize = 13.sp, color = Muted, lineHeight = 19.sp)

            // Key ingredients
            if (item.ingredients.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("KEY INGREDIENTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Muted, letterSpacing = 0.8.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.ingredients.forEach { ing ->
                            Text(
                                ing, fontSize = 10.sp, color = Muted,
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Size selection
            if (c.sizes.isNotEmpty()) {
                SectionCard("Size Selection") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        c.sizes.forEachIndexed { i, size ->
                            SizeTile(
                                label = size.label, extra = size.extraPrice,
                                selected = vm.sizeIdx == i,
                                modifier = Modifier.weight(1f)
                            ) { vm.sizeIdx = i }
                        }
                    }
                }
            }

            // Milk / spreads
            if (c.milkOptions.isNotEmpty()) {
                SectionCard("Milk Options / Spreads") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        c.milkOptions.forEachIndexed { i, m ->
                            OptionRow(m.name, m.extraPrice, vm.milkIdx == i) { vm.milkIdx = i }
                        }
                    }
                }
            }

            // Sugar levels / serving
            if (c.sugarLevels.isNotEmpty()) {
                SectionCard("Sugar Levels / Serving") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        c.sugarLevels.forEachIndexed { i, label ->
                            SugarChip(label, vm.sugarIdx == i) { vm.sugarIdx = i }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
        }

        // Bottom bar: quantity + Add to Cart
        Row(
            Modifier.fillMaxWidth().background(Cream).navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuantityStepper(vm.qty, onMinus = vm::decrement, onPlus = vm::increment)

            Box(
                Modifier.weight(1f).height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Rust, DarkRust)))
                    .clickable {
                        CartRepository.add(
                            CartLine(
                                itemId = item.id,
                                name = item.name,
                                imageUrl = item.imageUrl,
                                sizeLabel = c.sizes.getOrNull(vm.sizeIdx)?.label.orEmpty(),
                                sugar = c.sugarLevels.getOrNull(vm.sugarIdx).orEmpty(),
                                milkLabel = c.milkOptions.getOrNull(vm.milkIdx)?.name.orEmpty(),
                                unitPrice = vm.unitPrice(item),
                                qty = vm.qty
                            )
                        )
                        navController.navigate(Screen.cartScreen.route) {
                            popUpTo(Screen.detailScreen.route) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Add to Cart  •  " + vm.totalPrice(item).money(),
                    color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------- Pieces ----------
@Composable
private fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink, modifier = Modifier.size(20.dp)) }
}

@Composable
private fun DetailTopBar(liked: Boolean, onBack: () -> Unit, onToggleLike: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {

        // Back button
        Box(
            Modifier.align(Alignment.CenterStart).size(38.dp).clip(CircleShape)
                .background(Color.White).border(1.dp, Line, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink, modifier = Modifier.size(18.dp))
        }

        // Title
        Text(
            "ITEM CUSTOMIZER",
            Modifier.align(Alignment.Center),
            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Ink, letterSpacing = 0.5.sp
        )

        // Favorite button
        Box(
            Modifier.align(Alignment.CenterEnd).size(38.dp).clip(CircleShape)
                .background(if (liked) Color(0xFFFFE3E8) else Color.White)
                .border(1.dp, if (liked) Color(0xFFFFC2CE) else Line, CircleShape)
                .clickable(onClick = onToggleLike),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                "Favorite",
                tint = Color(0xFFE5365A),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape).background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Line, shape).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
        content()
    }
}

@Composable
private fun SizeTile(label: String, extra: Double, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier.clip(shape)
            .background(if (selected) Peach else Color.White)
            .border(1.dp, if (selected) Rust else Line, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            color = if (selected) Rust else Ink
        )
        Text(
            "+" + extra.money(), fontSize = 11.sp, fontFamily = FontFamily.Monospace,
            color = if (selected) Rust else Muted
        )
    }
}

@Composable
private fun OptionRow(label: String, extra: Double, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape)
            .background(if (selected) Peach else Color.White)
            .border(1.dp, if (selected) Rust else Line, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, Modifier.weight(1f), fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Rust else Muted
        )
        Text(
            "+" + extra.money(), fontSize = 12.sp, fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold, color = if (selected) Rust else Muted
        )
    }
}

@Composable
private fun SugarChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Text(
        label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = if (selected) Color.White else Muted,
        modifier = Modifier.clip(shape)
            .background(if (selected) Ink else Color.White)
            .border(1.dp, if (selected) Ink else Line, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun QuantityStepper(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.height(52.dp).clip(shape).background(Color.White).border(1.dp, Line, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton("−", onMinus)
        Text("$qty", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.widthIn(min = 24.dp), textAlign = TextAlign.Center)
        StepperButton("+", onPlus)
    }
}

@Composable
private fun StepperButton(label: String, onClick: () -> Unit) {
    Box(Modifier.size(44.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
    }
}