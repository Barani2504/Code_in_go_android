package com.simats.codeingo.ui.shop

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.AppDialog
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// ══════════════════════════════════════════════════════════════════
// 🛍️ GemShopSheet — Duolingo-inspired Gamified Cosmetic & Gem Store
// Exact 1:1 Parity with iOS BoneShopSheet.swift / GemShopSheet
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
    val price: Int, // 0 if learning unlock or default
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

    val featuredItems: List<CosmeticItem> get() = allItems.filter { it.isFeatured && it.category != CosmeticCategory.LIVES }

    fun item(forId: String): CosmeticItem? = allItems.find { it.id == forId }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemShopSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()
    val completedChapters by gameManager.completedChapters.collectAsState()
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

    // Try-On Live Preview State
    var tryingHeadwear by remember { mutableStateOf<String?>(null) }
    var tryingOutfit by remember { mutableStateOf<String?>(null) }
    var tryingAccessory by remember { mutableStateOf<String?>(null) }
    var tryingAura by remember { mutableStateOf<String?>(null) }

    val activeHeadwear = tryingHeadwear ?: equippedHeadwear
    val activeOutfit = tryingOutfit ?: equippedOutfit
    val activeAccessory = tryingAccessory ?: equippedAccessory
    val activeAuraId = tryingAura ?: equippedAura

    val isTryingAnything = tryingHeadwear != null || tryingOutfit != null || tryingAccessory != null || tryingAura != null

    var selectedCategory by remember { mutableStateOf(CosmeticCategory.HATS) }
    var previewItem by remember { mutableStateOf<CosmeticItem?>(null) }

    // Purchase Feedback Animations
    var showPurchaseCelebration by remember { mutableStateOf(false) }
    var purchasedItemName by remember { mutableStateOf("") }
    var floatingGemDeduction by remember { mutableStateOf<Int?>(null) }
    val floatingGemOffset = remember { Animatable(50f) }
    val floatingGemAlpha = remember { Animatable(1f) }
    var flyingItemEmoji by remember { mutableStateOf<String?>(null) }
    val flyingItemOffset = remember { Animatable(80f) }
    val flyingItemScale = remember { Animatable(0.5f) }

    // Alert Dialog State
    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

    // Character Idle Kinematics Hover Loop (matches iOS 2.0s -6.0dp hover)
    val infiniteTransition = rememberInfiniteTransition(label = "birdHover")
    val birdHoverOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "birdHoverY"
    )

    // Aura Color Engine
    val activeAuraColor = when (activeAuraId) {
        "aura_sparkle" -> AmberGold
        "aura_lightning" -> Color(0xFF00E5FF)
        "aura_rainbow" -> Color(0xFFAF52DE)
        "aura_frost" -> Color(0xFF007AFF)
        else -> Color(0xFFFF9500) // aura_ember or default
    }

    fun formattedNumber(n: Int): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(n)
    }

    fun isLearningRequirementMet(item: CosmeticItem): Boolean {
        return when (item.id) {
            "hat_bubble" -> completedChapters.contains(1) || prefs.getBoolean("completedBubbleSort", false)
            "outfit_wings" -> completedChapters.contains(2) || prefs.getBoolean("masteredLinkedList", false)
            "acc_pack" -> completedChapters.contains(3) || prefs.getBoolean("masteredStack", false)
            "acc_train" -> completedChapters.contains(4) || prefs.getBoolean("masteredQueue", false)
            "crown_tree" -> completedChapters.contains(5) || prefs.getBoolean("masteredBinaryTree", false)
            else -> false
        }
    }

    fun checkIsEquipped(item: CosmeticItem): Boolean {
        return when (item.category) {
            CosmeticCategory.HATS -> equippedHeadwear == item.id
            CosmeticCategory.OUTFITS -> equippedOutfit == item.id
            CosmeticCategory.ACCESSORIES -> equippedAccessory == item.id
            CosmeticCategory.EFFECTS -> equippedAura == item.id
            CosmeticCategory.LIVES -> false
        }
    }

    fun checkIsOwned(item: CosmeticItem): Boolean {
        if (item.category == CosmeticCategory.LIVES) return false
        if (item.price == 0 && item.unlockRequirement == null) return true
        if (item.isLearningUnlock) return isLearningRequirementMet(item)
        return ownedIds.contains(item.id)
    }

    fun isItemBeingTried(item: CosmeticItem): Boolean {
        return when (item.category) {
            CosmeticCategory.HATS -> tryingHeadwear == item.id
            CosmeticCategory.OUTFITS -> tryingOutfit == item.id
            CosmeticCategory.ACCESSORIES -> tryingAccessory == item.id
            CosmeticCategory.EFFECTS -> tryingAura == item.id
            CosmeticCategory.LIVES -> false
        }
    }

    fun tryItem(item: CosmeticItem) {
        if (item.category == CosmeticCategory.LIVES) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        when (item.category) {
            CosmeticCategory.HATS -> tryingHeadwear = item.id
            CosmeticCategory.OUTFITS -> tryingOutfit = item.id
            CosmeticCategory.ACCESSORIES -> tryingAccessory = item.id
            CosmeticCategory.EFFECTS -> tryingAura = item.id
            CosmeticCategory.LIVES -> {}
        }
    }

    fun resetTrying() {
        tryingHeadwear = null
        tryingOutfit = null
        tryingAccessory = null
        tryingAura = null
    }

    fun equip(item: CosmeticItem) {
        if (item.category == CosmeticCategory.LIVES) return
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        resetTrying()
        when (item.category) {
            CosmeticCategory.HATS -> {
                equippedHeadwear = item.id
                prefs.edit().putString("dsaEquippedHeadwear", item.id).apply()
            }
            CosmeticCategory.OUTFITS -> {
                equippedOutfit = item.id
                prefs.edit().putString("dsaEquippedOutfit", item.id).apply()
            }
            CosmeticCategory.ACCESSORIES -> {
                equippedAccessory = item.id
                prefs.edit().putString("dsaEquippedAccessory", item.id).apply()
            }
            CosmeticCategory.EFFECTS -> {
                equippedAura = item.id
                prefs.edit().putString("dsaEquippedAura", item.id).apply()
            }
            CosmeticCategory.LIVES -> {}
        }
    }

    fun unequip(item: CosmeticItem) {
        if (item.category == CosmeticCategory.LIVES) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        resetTrying()
        when (item.category) {
            CosmeticCategory.HATS -> {
                equippedHeadwear = "none_head"
                prefs.edit().putString("dsaEquippedHeadwear", "none_head").apply()
            }
            CosmeticCategory.OUTFITS -> {
                equippedOutfit = "none_outfit"
                prefs.edit().putString("dsaEquippedOutfit", "none_outfit").apply()
            }
            CosmeticCategory.ACCESSORIES -> {
                equippedAccessory = "none_acc"
                prefs.edit().putString("dsaEquippedAccessory", "none_acc").apply()
            }
            CosmeticCategory.EFFECTS -> {
                equippedAura = "none_aura"
                prefs.edit().putString("dsaEquippedAura", "none_aura").apply()
            }
            CosmeticCategory.LIVES -> {}
        }
    }

    fun buyHeartPack(amount: Int, price: Int, name: String) {
        if (heartsCount >= 10) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            alertTitle = "Hearts Full"
            alertMessage = "Your life hearts are already fully charged! (10/10 ❤️)"
            showAlert = true
            return
        }

        if (gemsCount < price) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            alertTitle = "Not Enough Diamonds"
            alertMessage = "You need ${price - gemsCount} more 💎 diamonds to buy $name. Complete more stages to earn diamonds!"
            showAlert = true
            return
        }

        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        purchasedItemName = "$name Refill"
        floatingGemDeduction = price
        flyingItemEmoji = "❤️"

        scope.launch {
            floatingGemOffset.snapTo(50f)
            floatingGemAlpha.snapTo(1f)
            flyingItemOffset.snapTo(80f)
            flyingItemScale.snapTo(0.5f)

            launch {
                floatingGemOffset.animateTo(-120f, tween(600, easing = FastOutSlowInEasing))
                floatingGemAlpha.animateTo(0f, tween(300))
            }
            launch {
                flyingItemOffset.animateTo(-100f, tween(600, easing = FastOutSlowInEasing))
                flyingItemScale.animateTo(1.3f, tween(600, easing = FastOutSlowInEasing))
            }

            delay(250)
            gameManager.buyHearts(amount, price)
            showPurchaseCelebration = true

            delay(950)
            floatingGemDeduction = null
            flyingItemEmoji = null

            delay(1300)
            showPurchaseCelebration = false
        }
    }

    fun buy(item: CosmeticItem) {
        if (item.category == CosmeticCategory.LIVES) {
            val amount = when (item.id) {
                "heart_pack_full" -> 10
                "heart_pack_5" -> 5
                else -> 1
            }
            buyHeartPack(amount, item.price, item.name)
            return
        }

        if (gemsCount < item.price) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            alertTitle = "Not Enough Gems"
            alertMessage = "You need ${item.price - gemsCount} more gems to buy ${item.name}. Complete Data Structure lessons to earn gems!"
            showAlert = true
            return
        }

        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        purchasedItemName = item.name
        floatingGemDeduction = item.price
        flyingItemEmoji = item.emoji

        scope.launch {
            floatingGemOffset.snapTo(50f)
            floatingGemAlpha.snapTo(1f)
            flyingItemOffset.snapTo(80f)
            flyingItemScale.snapTo(0.5f)

            launch {
                floatingGemOffset.animateTo(-120f, tween(600, easing = FastOutSlowInEasing))
                floatingGemAlpha.animateTo(0f, tween(300))
            }
            launch {
                flyingItemOffset.animateTo(-100f, tween(600, easing = FastOutSlowInEasing))
                flyingItemScale.animateTo(1.3f, tween(600, easing = FastOutSlowInEasing))
            }

            delay(250)
            gameManager.deductGems(item.price)
            val newOwned = "$ownedCosmeticsString,${item.id}"
            ownedCosmeticsString = newOwned
            prefs.edit().putString("dsaOwnedCosmetics", newOwned).apply()
            equip(item)
            showPurchaseCelebration = true

            delay(950)
            floatingGemDeduction = null
            flyingItemEmoji = null

            delay(1300)
            showPurchaseCelebration = false
        }
    }

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
            // ── 1. Shop Header Bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.90f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalDynamicThemeColors.current.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = Color(0xFF1CB0F6),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Gem Shop",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = LocalDynamicThemeColors.current.textPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                // Gem Balance Badge: 💎 balance
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1CB0F6).copy(alpha = 0.15f))
                        .border(1.5.dp, Color(0xFF1CB0F6).copy(alpha = 0.45f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("💎", fontSize = 16.sp)
                        Text(
                            text = formattedNumber(gemsCount),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1CB0F6)
                        )
                    }
                }
            }

            // Subtle divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFF1CB0F6).copy(alpha = 0.25f))
            )

            // ── Main Scrollable Shop Content ────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ── 2. Character Preview Card with Live Equipped / Tried Cosmetics ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(accentGlow = AmberGold.copy(alpha = 0.35f), cornerRadius = 22.dp)
                        .padding(vertical = 16.dp, horizontal = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Stage with Kinematic Mascot & Layered Cosmetics
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Friendly Pedestal Ring (Radial Gradient Aura)
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                activeAuraColor.copy(alpha = 0.30f),
                                                activeAuraColor.copy(alpha = 0.08f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // Shadow Ellipse Underneath
                            Box(
                                modifier = Modifier
                                    .size(width = 130.dp, height = 26.dp)
                                    .offset(y = 72.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.12f))
                            )

                            // Animated Bird Mascot with sinusoidal idle kinematics
                            Box(
                                modifier = Modifier.offset(y = birdHoverOffset.dp)
                            ) {
                                PhoenixAnimatedMascotView(
                                    pose = PhoenixMascotPose.Welcoming,
                                    size = 155.dp
                                )
                            }

                            // Layer 1: Headwear (Above head)
                            if (activeHeadwear != "none_head") {
                                CosmeticsCatalog.item(activeHeadwear)?.emoji?.let { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 44.sp,
                                        modifier = Modifier
                                            .offset(x = 2.dp, y = (-56 + birdHoverOffset).dp)
                                    )
                                }
                            }

                            // Layer 2: Outfit (Body)
                            if (activeOutfit != "none_outfit") {
                                CosmeticsCatalog.item(activeOutfit)?.emoji?.let { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 34.sp,
                                        modifier = Modifier
                                            .offset(x = 0.dp, y = (18 + birdHoverOffset).dp)
                                    )
                                }
                            }

                            // Layer 3: Accessory (Face/Eyes)
                            if (activeAccessory != "none_acc") {
                                CosmeticsCatalog.item(activeAccessory)?.emoji?.let { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 34.sp,
                                        modifier = Modifier
                                            .offset(x = 16.dp, y = (-8 + birdHoverOffset).dp)
                                    )
                                }
                            }

                            // Layer 4: Aura Emitters (Floating ambient effects)
                            if (activeAuraId != "none_aura") {
                                CosmeticsCatalog.item(activeAuraId)?.emoji?.let { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 22.sp,
                                        modifier = Modifier.offset(x = (-60).dp, y = (-26 + birdHoverOffset).dp)
                                    )
                                    Text(
                                        text = emoji,
                                        fontSize = 22.sp,
                                        modifier = Modifier.offset(x = 58.dp, y = (22 + birdHoverOffset).dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Tag underneath
                        if (isTryingAnything) {
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "👀 Trying on preview",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1CB0F6)
                                )

                                Box(
                                    modifier = Modifier
                                        .pressScale()
                                        .clip(CircleShape)
                                        .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))
                                        .clickable { resetTrying() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Reset",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LocalDynamicThemeColors.current.textSecondary
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Your stylish Phoenix companion",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalDynamicThemeColors.current.textSecondary
                            )
                        }
                    }
                }

                // ── 2.5 Life Hearts & Health Refill Station ─────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(accentGlow = DuolingoRed.copy(alpha = 0.35f), cornerRadius = 20.dp)
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFFF4057),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "LIFE HEARTS REFILL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = LocalDynamicThemeColors.current.textSecondary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Current Heart Count Badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4057).copy(alpha = 0.12f))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("❤️", fontSize = 12.sp)
                                    Text(
                                        text = "$heartsCount/10",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFF4057)
                                    )
                                }
                            }
                        }

                        // 3 Quick-Buy Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // +1 Heart
                            HeartRefillCard(
                                title = "+1 Heart",
                                emoji = "💓",
                                price = 5,
                                subtitle = "Emergency +1",
                                isHighlighted = false,
                                modifier = Modifier.weight(1f),
                                onClick = { buyHeartPack(1, 5, "+1 Heart") }
                            )

                            // +5 Hearts
                            HeartRefillCard(
                                title = "+5 Hearts",
                                emoji = "❤️",
                                price = 20,
                                subtitle = "Half Refill",
                                isHighlighted = false,
                                modifier = Modifier.weight(1f),
                                onClick = { buyHeartPack(5, 20, "+5 Hearts") }
                            )

                            // Full 10/10
                            HeartRefillCard(
                                title = "Full 10/10",
                                emoji = "💖",
                                price = 35,
                                subtitle = "Max Capacity",
                                isHighlighted = true,
                                modifier = Modifier.weight(1f),
                                onClick = { buyHeartPack(10, 35, "Full 10/10") }
                            )
                        }
                    }
                }

                // ── 3. Featured Section ─────────────────────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("⭐", fontSize = 14.sp)
                        Text(
                            text = "FEATURED",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = LocalDynamicThemeColors.current.textSecondary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CosmeticsCatalog.featuredItems.forEach { item ->
                            FeaturedItemCard(
                                item = item,
                                isEquipped = checkIsEquipped(item),
                                isOwned = checkIsOwned(item),
                                modifier = Modifier.weight(1f),
                                onClick = { previewItem = item }
                            )
                        }
                    }
                }

                // ── 4. 5 Category Filter Tabs ───────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CosmeticCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .pressScale()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) DuolingoGreen else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) DuolingoGreen else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(category.iconEmoji, fontSize = 13.sp)
                                Text(
                                    text = category.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                        }
                    }
                }

                // ── 5. Item Cards Grid for Selected Category ────────────────────────
                val filteredItems = CosmeticsCatalog.allItems.filter { it.category == selectedCategory }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredItems.forEach { item ->
                        ItemRowCard(
                            item = item,
                            isEquipped = checkIsEquipped(item),
                            isOwned = checkIsOwned(item),
                            isLocked = item.isLearningUnlock && !isLearningRequirementMet(item),
                            onClick = { previewItem = item }
                        )
                    }
                }

                // ── 6. Reward Philosophy Banner ("HOW TO EARN GEMS") ────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(accentGlow = Color(0xFF00E5FF).copy(alpha = 0.25f), cornerRadius = 20.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("💡", fontSize = 16.sp)
                            Text(
                                text = "HOW TO EARN GEMS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1CB0F6)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            EarningRow("Complete Lesson", "+10 Gems")
                            EarningRow("Perfect Quiz Score (100%)", "+25 Gems")
                            EarningRow("Daily Coding Challenge", "+30 Gems")
                            EarningRow("Defeat Boss Algorithm", "+100 Gems")
                        }

                        Text(
                            text = "All shop items are cosmetic only. Gems are earned 100% by learning!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalDynamicThemeColors.current.textSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // ── Purchase Celebration Top Overlay Banner ─────────────────────────
        AnimatedVisibility(
            visible = showPurchaseCelebration,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp, start = 16.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DuolingoGreen)
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎉", fontSize = 24.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "New Item Unlocked!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "$purchasedItemName is now in your wardrobe",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // ── Floating 💎 -Price Deduction Animation ──────────────────────────
        floatingGemDeduction?.let { deduction ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = floatingGemOffset.value.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(6.dp, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "💎 -$deduction",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1CB0F6)
                )
            }
        }

        // ── Flying Item Emoji Toward Bird Mascot ────────────────────────────
        flyingItemEmoji?.let { emoji ->
            Text(
                text = emoji,
                fontSize = 48.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = flyingItemOffset.value.dp)
                    .scale(flyingItemScale.value)
            )
        }

        // ── Try Before Buying Modal Sheet ───────────────────────────────────
        previewItem?.let { item ->
            val isEquipped = checkIsEquipped(item)
            val isOwned = checkIsOwned(item)
            val isLocked = item.isLearningUnlock && !isLearningRequirementMet(item)
            val isCurrentlyTrying = isItemBeingTried(item)

            ModalBottomSheet(
                onDismissRequest = { previewItem = null },
                containerColor = if (isDark) Color(0xFF131D2D) else Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item Emoji Circle Display
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                            .border(1.5.dp, Color.Black.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.emoji, fontSize = 46.sp)
                    }

                    // Name & Description
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )

                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalDynamicThemeColors.current.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        if (item.unlockRequirement != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = if (isLocked) "🔒 Unlock: ${item.unlockRequirement}" else "✓ Requirement Mastered!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLocked) Color(0xFFE66619) else Color(0xFF47A403)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Action Buttons (Equip / Try / Buy)
                    when {
                        item.category == CosmeticCategory.LIVES -> {
                            Duolingo3DButton(
                                title = "BUY & RESTORE HEARTS 💎 ${item.price}",
                                style = Duolingo3DButtonColor.AMBER,
                                onClick = {
                                    buy(item)
                                    previewItem = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        isEquipped -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressScale()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                                    .clickable {
                                        unequip(item)
                                        previewItem = null
                                    }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Unequip",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocalDynamicThemeColors.current.textSecondary
                                )
                            }
                        }
                        isLocked -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Complete lessons to unlock this reward for free!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = LocalDynamicThemeColors.current.textSecondary
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pressScale()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                                        .clickable {
                                            tryItem(item)
                                            previewItem = null
                                        }
                                        .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                    Text(
                                        text = if (isCurrentlyTrying) "Currently Trying" else "TRY ON PHOENIX",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1CB0F6)
                                    )
                                }
                            }
                        }
                        isOwned -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pressScale()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                                        .clickable {
                                            tryItem(item)
                                            previewItem = null
                                        }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "TRY",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1CB0F6)
                                    )
                                }

                                Duolingo3DButton(
                                    title = "EQUIP",
                                    style = Duolingo3DButtonColor.GREEN,
                                    onClick = {
                                        equip(item)
                                        previewItem = null
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        else -> {
                            // Not Owned: Show TRY and BUY side-by-side
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pressScale()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                                        .clickable {
                                            tryItem(item)
                                            previewItem = null
                                        }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "TRY",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1CB0F6)
                                    )
                                }

                                Duolingo3DButton(
                                    title = "BUY 💎 ${item.price}",
                                    style = Duolingo3DButtonColor.BLUE,
                                    onClick = {
                                        buy(item)
                                        previewItem = null
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Alert Modal Dialog ──────────────────────────────────────────────
        if (showAlert) {
            AppDialog(
                onDismissRequest = { showAlert = false },
                accentGlow = DuolingoRed.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = alertTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )

                    Text(
                        text = alertMessage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Duolingo3DButton(
                        title = "OK",
                        style = Duolingo3DButtonColor.GREEN,
                        onClick = { showAlert = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ── Heart Refill Card Composable ────────────────────────────────────────────
@Composable
private fun HeartRefillCard(
    title: String,
    emoji: String,
    price: Int,
    subtitle: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark

    Box(
        modifier = modifier
            .pressScale()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
            .border(
                if (isHighlighted) 1.5.dp else 1.dp,
                if (isHighlighted) Color(0xFFFF4057).copy(alpha = 0.70f) else Color.Transparent,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(emoji, fontSize = 26.sp)

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = LocalDynamicThemeColors.current.textSecondary,
                maxLines = 1
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isHighlighted) Color(0xFFFF4057).copy(alpha = 0.20f)
                        else (if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text("💎", fontSize = 10.sp)
                    Text(
                        text = "$price",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1CB0F6)
                    )
                }
            }
        }
    }
}

// ── Featured Cosmetic Card ──────────────────────────────────────────────────
@Composable
private fun FeaturedItemCard(
    item: CosmeticItem,
    isEquipped: Boolean,
    isOwned: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val glowColor = if (isEquipped) DuolingoGreen else (if (isOwned) Color.Cyan else AmberGold)

    Box(
        modifier = modifier
            .pressScale()
            .liquidGlassCard(accentGlow = glowColor.copy(alpha = 0.35f), cornerRadius = 18.dp)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Emoji Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 34.sp)
            }

            // Name
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Status or Price
            when {
                isEquipped -> {
                    Text(
                        text = "✓ Equipped",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF47A403)
                    )
                }
                isOwned -> {
                    Text(
                        text = "✓ Owned",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1CB0F6)
                    )
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text("💎", fontSize = 11.sp)
                        Text(
                            text = "${item.price}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1CB0F6)
                        )
                    }
                }
            }
        }
    }
}

// ── Catalog Item Row Card ───────────────────────────────────────────────────
@Composable
private fun ItemRowCard(
    item: CosmeticItem,
    isEquipped: Boolean,
    isOwned: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val glowColor = if (isEquipped) DuolingoGreen else (if (isOwned) Color.Cyan else (if (isLocked) Color.Transparent else Color(0xFF1CB0F6).copy(alpha = 0.35f)))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale()
            .liquidGlassCard(accentGlow = glowColor, cornerRadius = 18.dp)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Preview Emoji Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
                    .border(
                        if (isEquipped) 2.dp else 1.dp,
                        if (isEquipped) DuolingoGreen.copy(alpha = 0.6f) else (if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.scale(if (isLocked) 0.85f else 1f)
                )

                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = LocalDynamicThemeColors.current.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Name & Description
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) LocalDynamicThemeColors.current.textSecondary else LocalDynamicThemeColors.current.textPrimary
                )

                if (item.unlockRequirement != null && isLocked) {
                    Text(
                        text = "🔒 ${item.unlockRequirement}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE66619)
                    )
                } else {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Status or Price Pill
            when {
                isEquipped -> {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF58CC02).copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF47A403),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Equipped",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF47A403)
                            )
                        }
                    }
                }
                isLocked -> {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = LocalDynamicThemeColors.current.textSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Locked",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalDynamicThemeColors.current.textSecondary
                            )
                        }
                    }
                }
                isOwned -> {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF1CB0F6),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Owned",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1CB0F6)
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1CB0F6).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFF1CB0F6).copy(alpha = 0.35f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💎", fontSize = 12.sp)
                            Text(
                                text = "${item.price}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1CB0F6)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Philosophy Row ──────────────────────────────────────────────────────────
@Composable
private fun EarningRow(title: String, reward: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("→", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1CB0F6))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = reward,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1CB0F6)
        )
    }
}

// ── Backward Compatibility Alias ───────────────────────────────────────────
@Composable
fun BoneShopSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) = GemShopSheet(onDismiss = onDismiss, modifier = modifier)
