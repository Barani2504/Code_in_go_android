package com.simats.duolingo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

data class ShopItem(
    val emoji: String,
    val title: String,
    val description: String,
    val price: String,
    val priceColor: Color = DuolingoBlue,
    val isPurchased: Boolean = false,
)

private val shopItems = listOf(
    ShopItem("❤️", "Heart Refill",    "Restore all 5 hearts instantly", "350 💎", DuolingoOrange),
    ShopItem("❄️", "Streak Freeze",   "Protects your streak for 1 day", "200 💎", DuolingoBlue),
    ShopItem("🔥", "Double XP",       "Earn 2× XP for 15 minutes",      "100 💎", DuolingoGreen),
    ShopItem("🎨", "Theme: Dark Pro", "Unlock the Pro dark theme",       "FREE",   DuolingoGreen, isPurchased = true),
    ShopItem("🏆", "Trophy Badge",    "Show off your rank to friends",   "500 💎", AmberGold),
)

@Composable
fun ShopScreen(onOpenCreateProfile: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Gem balance header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Shop", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("💎", fontSize = 18.sp)
                Text("500", color = DuolingoBlue, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }

        // Section label
        Text(
            "⚡  POWER-UPS",
            color = DuolingoSubtext,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
        )

        // Shop items list
        shopItems.forEach { item ->
            ShopItemCard(item = item, onOpenCreateProfile = onOpenCreateProfile)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ShopItemCard(item: ShopItem, onOpenCreateProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(item.emoji, fontSize = 34.sp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(item.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(item.description, color = DuolingoSubtext, fontSize = 12.sp)
        }
        if (item.isPurchased) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DuolingoGreen.copy(0.12f))
                    .border(1.dp, DuolingoGreen, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("✅", fontSize = 14.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.priceColor.copy(0.12f))
                    .border(1.dp, item.priceColor.copy(0.5f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onOpenCreateProfile)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(item.price, color = item.priceColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
