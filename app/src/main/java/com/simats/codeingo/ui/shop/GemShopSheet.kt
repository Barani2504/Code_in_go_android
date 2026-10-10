package com.simats.codeingo.ui.shop

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════
// 🛍️ GemShopSheet — Duolingo-inspired Gamified Cosmetic & Gem Store
// Exact parity with iOS BoneShopSheet.swift / GemShopSheet
// ══════════════════════════════════════════════════════════════════

enum class CosmeticCategory(val label: String, val iconEmoji: String) {
    HATS("Hats", "🎓"),
    OUTFITS("Outfits", "🧥"),
    ACCESSORIES("Accessories", "👓"),
    EFFECTS("Effects", "✨"),
    LIVES("Hearts", "❤️")
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val category: CosmeticCategory,
    val emoji: String,
    val price: Int,
    val description: String,
    val unlockRequirement: String? = null,
    val isFeatured: Boolean = false
) {
    val isLearningUnlock: Boolean get() = unlockRequirement != null
}

object CosmeticsCatalog {
    val allItems: List<CosmeticItem> = listOf(
        // ── 1. HATS ────────────────────────────────────────────────────────
        CosmeticItem("none_head", "No Hat", CosmeticCategory.HATS, "✖️", 0, "Natural feathers with no headwear"),
        CosmeticItem("hat_dev", "Developer Hat", CosmeticCategory.HATS, "🎩", 250, "A refined top hat for elegant code architecture", isFeatured = true),
        CosmeticItem("cap_code", "Coder Cap", CosmeticCategory.HATS, "🧢", 150, "Backwards baseball cap for marathon debugging", isFeatured = true),
        CosmeticItem("crown_gold", "Data Master Crown", CosmeticCategory.HATS, "👑", 500, "Supreme royalty of asymptotic analysis", isFeatured = true),
        CosmeticItem("cap_grad", "Graduation Cap", CosmeticCategory.HATS, "🎓", 250, "Degree in Algorithm Optimization"),
        CosmeticItem("hat_wizard", "Wizard Hat", CosmeticCategory.HATS, "🧙‍♂️", 400, "Casts O(1) constant time optimization spells"),
        CosmeticItem("hat_party", "Party Hat", CosmeticCategory.HATS, "🥳", 100, "Celebrates unbroken practice streaks"),
        CosmeticItem("hat_bubble", "Bubble Hat", CosmeticCategory.HATS, "🫧", 0, "Floating soap bubbles of sorted elements", unlockRequirement = "Complete Bubble Sort"),
        CosmeticItem("crown_tree", "Tree Crown", CosmeticCategory.HATS, "🌳", 0, "Living leaves from the root of a balanced BST", unlockRequirement = "Master Binary Tree"),

        // ── 2. OUTFITS ─────────────────────────────────────────────────────
        CosmeticItem("none_outfit", "No Outfit", CosmeticCategory.OUTFITS, "✖️", 0, "Classic wild bird plumage"),
        CosmeticItem("outfit_hoodie", "Hacker Hoodie", CosmeticCategory.OUTFITS, "🧥", 350, "Comfortable fleece for late night coding sessions"),
        CosmeticItem("outfit_cape", "Superhero Cape", CosmeticCategory.OUTFITS, "🦸", 450, "Flows heroically through pointer traversals"),
        CosmeticItem("outfit_vest", "Refactor Vest", CosmeticCategory.OUTFITS, "🦺", 200, "High visibility caution while cleaning legacy code"),
        CosmeticItem("outfit_belt", "Black Belt Gi", CosmeticCategory.OUTFITS, "🥋", 550, "Grandmaster of competitive programming"),
        CosmeticItem("outfit_wings", "Link Wings", CosmeticCategory.OUTFITS, "🪽", 0, "Nodes linked together into shining wings", unlockRequirement = "Master Linked List"),

        // ── 3. ACCESSORIES ─────────────────────────────────────────────────
        CosmeticItem("none_acc", "No Accessory", CosmeticCategory.ACCESSORIES, "✖️", 0, "Clear and unadorned companion"),
        CosmeticItem("glasses_nerd", "Nerd Glasses", CosmeticCategory.ACCESSORIES, "👓", 200, "Instantly spots subtle off-by-one boundary bugs"),
        CosmeticItem("shades_cool", "Cool Shades", CosmeticCategory.ACCESSORIES, "🕶️", 300, "Ultra-chill O(1) lookup confidence"),
        CosmeticItem("scarf_red", "Warm Scarf", CosmeticCategory.ACCESSORIES, "🧣", 180, "Shields against freezing stack overflows"),
        CosmeticItem("bowtie_ruby", "Ruby Bowtie", CosmeticCategory.ACCESSORIES, "🎀", 220, "Dapper style for your linked-list nodes"),
        CosmeticItem("acc_pack", "Stack Backpack", CosmeticCategory.ACCESSORIES, "🎒", 0, "Holds an infinite LIFO stack of study books", unlockRequirement = "Master Stack"),
        CosmeticItem("acc_train", "Train Accessory", CosmeticCategory.ACCESSORIES, "🚂", 0, "Little engine that keeps all queue passengers moving", unlockRequirement = "Master Queue"),

        // ── 4. EFFECTS ─────────────────────────────────────────────────────
        CosmeticItem("none_aura", "No Effect", CosmeticCategory.EFFECTS, "✖️", 0, "Natural peaceful aura"),
        CosmeticItem("aura_ember", "Phoenix Embers", CosmeticCategory.EFFECTS, "🔥", 0, "Warm flames of algorithmic knowledge"),
        CosmeticItem("aura_sparkle", "Golden Sparkles", CosmeticCategory.EFFECTS, "✨", 280, "Radiant sparkles of clean code perfection"),
        CosmeticItem("aura_lightning", "Lightning Flash", CosmeticCategory.EFFECTS, "⚡", 420, "Sub-millisecond runtime electricity"),
        CosmeticItem("aura_rainbow", "Rainbow Stream", CosmeticCategory.EFFECTS, "🌈", 600, "Full spectral spectrum of memory traversal"),
        CosmeticItem("aura_frost", "Cache Frost", CosmeticCategory.EFFECTS, "❄️", 320, "Cool icy air keeping processor temperature low"),

        // ── 5. LIFE HEARTS & HEALTH REFILLS ────────────────────────────────
        CosmeticItem("heart_pack_full", "Full Heart Refill (10/10)", CosmeticCategory.LIVES, "💖", 35, "Instantly refills all 10 life hearts to full capacity", isFeatured = true),
        CosmeticItem("heart_pack_5", "+5 Life Hearts", CosmeticCategory.LIVES, "❤️", 20, "Restores +5 hearts so you can keep practicing without pause"),
        CosmeticItem("heart_pack_1", "+1 Life Heart", CosmeticCategory.LIVES, "💓", 5, "Emergency +1 single life heart refill")
    )

    val featuredItems: List<CosmeticItem> get() = allItems.filter { it.isFeatured }

    fun item(forId: String): CosmeticItem? = allItems.find { it.id == forId }
}

@Composable
fun GemShopSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()
    val scope = rememberCoroutineScope()
    val isDark = LocalDynamicThemeColors.current.isDark

    val prefs = remember { context.getSharedPreferences("dsa_cosmetics_prefs", Context.MODE_PRIVATE) }

    // Equipped state
    var equippedHeadwear by remember { mutableStateOf(prefs.getString("dsaEquippedHeadwear", "cap_grad") ?: "cap_grad") }
    var equippedOutfit by remember { mutableStateOf(prefs.getString("dsaEquippedOutfit", "none_outfit") ?: "none_outfit") }
    var equippedAccessory by remember { mutableStateOf(prefs.getString("dsaEquippedAccessory", "glasses_nerd") ?: "glasses_nerd") }
    var equippedAura by remember { mutableStateOf(prefs.getString("dsaEquippedAura", "aura_ember") ?: "aura_ember") }
    var ownedCosmeticsString by remember {
        mutableStateOf(
            prefs.getString("dsaOwnedCosmetics", "none_head,none_outfit,none_acc,none_aura,cap_grad,glasses_nerd,aura_ember,hat_party")
                ?: "none_head,none_outfit,none_acc,none_aura,cap_grad,glasses_nerd,aura_ember,hat_party"
        )
    }

    val ownedIds = remember(ownedCosmeticsString) {
        ownedCosmeticsString.split(",").filter { it.isNotEmpty() }.toSet()
    }

    // Try-On Preview State
    var tryingHeadwear by remember { mutableStateOf<String?>(null) }
    var tryingOutfit by remember { mutableStateOf<String?>(null) }
    var tryingAccessory by remember { mutableStateOf<String?>(null) }
    var tryingAura by remember { mutableStateOf<String?>(null) }

    val activeHeadwear = tryingHeadwear ?: equippedHeadwear
    val activeOutfit = tryingOutfit ?: equippedOutfit
    val activeAccessory = tryingAccessory ?: equippedAccessory
    val activeAura = tryingAura ?: equippedAura

    val isTryingOn = tryingHeadwear != null || tryingOutfit != null || tryingAccessory != null || tryingAura != null

    var selectedCategory by remember { mutableStateOf(CosmeticCategory.HATS) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastIsSuccess by remember { mutableStateOf(true) }

    fun showToast(msg: String, isSuccess: Boolean) {
        scope.launch {
            toastMessage = msg
            toastIsSuccess = isSuccess
            delay(2800)
            toastMessage = null
        }
    }

    fun saveEquipped(type: String, id: String) {
        when (type) {
            "head" -> { equippedHeadwear = id; prefs.edit().putString("dsaEquippedHeadwear", id).apply() }
            "outfit" -> { equippedOutfit = id; prefs.edit().putString("dsaEquippedOutfit", id).apply() }
            "acc" -> { equippedAccessory = id; prefs.edit().putString("dsaEquippedAccessory", id).apply() }
            "aura" -> { equippedAura = id; prefs.edit().putString("dsaEquippedAura", id).apply() }
        }
    }

    fun buyItem(item: CosmeticItem) {
        if (item.category == CosmeticCategory.LIVES) {
            val amount = when (item.id) {
                "heart_pack_full" -> 10
                "heart_pack_5" -> 5
                else -> 1
            }
            val (success, message) = gameManager.buyHearts(amount, item.price)
            showToast(message, success)
            return
        }

        if (ownedIds.contains(item.id)) {
            showToast("You already own ${item.name}!", true)
            return
        }

        if (gemsCount < item.price) {
            showToast("Not enough diamonds! You need ${item.price - gemsCount} more 💎.", false)
            return
        }

        // Deduct gems
        val (success, _) = gameManager.buyHearts(0, item.price)
        val newOwned = "$ownedCosmeticsString,${item.id}"
        ownedCosmeticsString = newOwned
        prefs.edit().putString("dsaOwnedCosmetics", newOwned).apply()

        // Auto-equip purchased item
        when (item.category) {
            CosmeticCategory.HATS -> { saveEquipped("head", item.id); tryingHeadwear = null }
            CosmeticCategory.OUTFITS -> { saveEquipped("outfit", item.id); tryingOutfit = null }
            CosmeticCategory.ACCESSORIES -> { saveEquipped("acc", item.id); tryingAccessory = null }
            CosmeticCategory.EFFECTS -> { saveEquipped("aura", item.id); tryingAura = null }
            CosmeticCategory.LIVES -> {}
        }

        showToast("🎉 Purchased and equipped ${item.name}!", true)
    }

    // Mascot Idle Floating Animation
    val infiniteTransition = rememberInfiniteTransition(label = "shopIdle")
    val mascotFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── 1. Top Header Bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalDynamicThemeColors.current.textPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🛍️", fontSize = 18.sp)
                    Text(
                        text = "GEM SHOP",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                }

                // Live Diamond Currency Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Cyan.copy(alpha = 0.15f))
                        .border(1.dp, Color.Cyan.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("💎", fontSize = 12.sp)
                        Text(
                            text = "$gemsCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            }

            // ── Main Scrollable Shop Content ────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 2. Character Preview Card ───────────────────────────────────
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    accentGlow = AmberGold.copy(alpha = 0.20f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YOUR COMPANION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = AmberGold
                            )

                            if (isTryingOn) {
                                Box(
                                    modifier = Modifier
                                        .pressScale()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AmberGold.copy(alpha = 0.20f))
                                        .border(1.dp, AmberGold, RoundedCornerShape(12.dp))
                                        .clickable {
                                            tryingHeadwear = null
                                            tryingOutfit = null
                                            tryingAccessory = null
                                            tryingAura = null
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "RESET TRY ↩️",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mascot Visual Stage with Dynamic Cosmetics
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .offset(y = mascotFloatY.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Glowing Aura Behind
                            val auraColor = when (activeAura) {
                                "aura_sparkle" -> AmberGold
                                "aura_lightning" -> Color.Cyan
                                "aura_rainbow" -> Color(0xFFB946FA)
                                "aura_frost" -> Color(0xFF80D8FF)
                                else -> Color(0xFFFF5722)
                            }
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .blur(20.dp)
                                    .clip(CircleShape)
                                    .background(auraColor.copy(alpha = 0.40f))
                            )

                            // Main Mascot Emoji
                            Text("🦅", fontSize = 64.sp)

                            // Headwear Emoji (Above Head)
                            val headEmoji = CosmeticsCatalog.item(activeHeadwear)?.emoji
                            if (!headEmoji.isNullOrEmpty() && headEmoji != "✖️") {
                                Text(
                                    text = headEmoji,
                                    fontSize = 32.sp,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = (-12).dp)
                                )
                            }

                            // Accessory Emoji (Center Eyes)
                            val accEmoji = CosmeticsCatalog.item(activeAccessory)?.emoji
                            if (!accEmoji.isNullOrEmpty() && accEmoji != "✖️") {
                                Text(
                                    text = accEmoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = (-4).dp)
                                )
                            }

                            // Outfit Emoji (Bottom Body)
                            val outfitEmoji = CosmeticsCatalog.item(activeOutfit)?.emoji
                            if (!outfitEmoji.isNullOrEmpty() && outfitEmoji != "✖️") {
                                Text(
                                    text = outfitEmoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status subtitle
                        Text(
                            text = if (isTryingOn) "✨ PREVIEWING TRY-ON" else "EQUIPPED IN SANCTUARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTryingOn) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.60f)
                        )
                    }
                }

                // ── 3. Life Hearts & Health Refill Station ─────────────────────────
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    accentGlow = DuolingoRed.copy(alpha = 0.20f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("❤️", fontSize = 16.sp)
                                Text(
                                    text = "LIFE HEARTS REFILL",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                            Text(
                                text = "$heartsCount/10 Hearts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DuolingoRed
                            )
                        }

                        Text(
                            text = "Refill life hearts instantly with diamonds to keep practicing without waiting.",
                            fontSize = 11.sp,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CosmeticsCatalog.allItems.filter { it.category == CosmeticCategory.LIVES }.forEach { heartItem ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pressScale()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, DuolingoRed.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                        .clickable { buyItem(heartItem) }
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(heartItem.emoji, fontSize = 22.sp)
                                        Text(
                                            text = heartItem.name.substringBefore(" ("),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LocalDynamicThemeColors.current.textPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(DuolingoRed.copy(alpha = 0.20f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "💎 ${heartItem.price}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFFF4B4B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── 4. Category Pills Filter ────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CosmeticCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .pressScale()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) AmberGold else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AmberGold else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(cat.iconEmoji, fontSize = 13.sp)
                                Text(
                                    text = cat.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.Black else LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                        }
                    }
                }

                // ── 5. Items Grid for Selected Category ─────────────────────────
                val items = CosmeticsCatalog.allItems.filter { it.category == selectedCategory }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.forEach { item ->
                        val isOwned = ownedIds.contains(item.id) || item.price == 0
                        val isEquipped = when (item.category) {
                            CosmeticCategory.HATS -> equippedHeadwear == item.id
                            CosmeticCategory.OUTFITS -> equippedOutfit == item.id
                            CosmeticCategory.ACCESSORIES -> equippedAccessory == item.id
                            CosmeticCategory.EFFECTS -> equippedAura == item.id
                            CosmeticCategory.LIVES -> false
                        }
                        val isBeingTried = when (item.category) {
                            CosmeticCategory.HATS -> tryingHeadwear == item.id
                            CosmeticCategory.OUTFITS -> tryingOutfit == item.id
                            CosmeticCategory.ACCESSORIES -> tryingAccessory == item.id
                            CosmeticCategory.EFFECTS -> tryingAura == item.id
                            CosmeticCategory.LIVES -> false
                        }

                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            accentGlow = if (isEquipped) DuolingoGreen.copy(alpha = 0.25f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Emoji Box
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.emoji, fontSize = 26.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Info
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = item.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LocalDynamicThemeColors.current.textPrimary
                                        )
                                        if (item.isFeatured) {
                                            Text("⭐", fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.unlockRequirement ?: item.description,
                                        fontSize = 11.sp,
                                        color = if (item.isLearningUnlock) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.60f),
                                        lineHeight = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Actions (Equip / Try / Buy)
                                when {
                                    item.category == CosmeticCategory.LIVES -> {
                                        Box(
                                            modifier = Modifier
                                                .pressScale()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DuolingoRed)
                                                .clickable { buyItem(item) }
                                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                text = "💎 ${item.price}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    isEquipped -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DuolingoGreen.copy(alpha = 0.20f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "EQUIPPED",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = DuolingoGreen
                                            )
                                        }
                                    }
                                    isOwned -> {
                                        Box(
                                            modifier = Modifier
                                                .pressScale()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DuolingoBlue)
                                                .clickable {
                                                    when (item.category) {
                                                        CosmeticCategory.HATS -> { saveEquipped("head", item.id); tryingHeadwear = null }
                                                        CosmeticCategory.OUTFITS -> { saveEquipped("outfit", item.id); tryingOutfit = null }
                                                        CosmeticCategory.ACCESSORIES -> { saveEquipped("acc", item.id); tryingAccessory = null }
                                                        CosmeticCategory.EFFECTS -> { saveEquipped("aura", item.id); tryingAura = null }
                                                        CosmeticCategory.LIVES -> {}
                                                    }
                                                    showToast("Equipped ${item.name}!", true)
                                                }
                                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                text = "EQUIP",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    item.isLearningUnlock -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White.copy(alpha = 0.08f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "🔒 LOCKED",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.50f)
                                            )
                                        }
                                    }
                                    else -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Try Button
                                            Box(
                                                modifier = Modifier
                                                    .pressScale()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isBeingTried) AmberGold.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isBeingTried) AmberGold else Color.White.copy(alpha = 0.15f),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        when (item.category) {
                                                            CosmeticCategory.HATS -> tryingHeadwear = if (isBeingTried) null else item.id
                                                            CosmeticCategory.OUTFITS -> tryingOutfit = if (isBeingTried) null else item.id
                                                            CosmeticCategory.ACCESSORIES -> tryingAccessory = if (isBeingTried) null else item.id
                                                            CosmeticCategory.EFFECTS -> tryingAura = if (isBeingTried) null else item.id
                                                            CosmeticCategory.LIVES -> {}
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                            ) {
                                                Text(
                                                    text = if (isBeingTried) "TRYING" else "TRY",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isBeingTried) AmberGold else LocalDynamicThemeColors.current.textPrimary
                                                )
                                            }

                                            // Buy Button
                                            Box(
                                                modifier = Modifier
                                                    .pressScale()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(AmberGold)
                                                    .clickable { buyItem(item) }
                                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                            ) {
                                                Text(
                                                    text = "💎 ${item.price}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ── Floating Toast Overlay ──────────────────────────────────────────
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { 60 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { 60 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        ) {
            toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131D2D))
                        .border(
                            1.5.dp,
                            if (toastIsSuccess) DuolingoGreen else DuolingoRed,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
