package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

fun getPhoenixDrawableId(stageId: Int): Int {
    return when (stageId) {
        1 -> R.drawable.phoenix_stage_1
        2 -> R.drawable.phoenix_stage_2
        3 -> R.drawable.phoenix_stage_3
        4 -> R.drawable.phoenix_stage_4
        5 -> R.drawable.phoenix_stage_5
        6 -> R.drawable.phoenix_stage_6
        7 -> R.drawable.phoenix_stage_7
        8 -> R.drawable.phoenix_stage_8
        9 -> R.drawable.phoenix_stage_9
        10 -> R.drawable.phoenix_stage_10
        11 -> R.drawable.phoenix_stage_11
        12 -> R.drawable.phoenix_stage_12
        13 -> R.drawable.phoenix_stage_13
        14 -> R.drawable.phoenix_stage_14
        15 -> R.drawable.phoenix_stage_15
        16 -> R.drawable.phoenix_stage_16
        17 -> R.drawable.phoenix_stage_17
        18 -> R.drawable.phoenix_stage_18
        else -> R.drawable.phoenix_stage_1
    }
}

data class PhoenixStage(
    val id: Int,
    val name: String,
    val subtitle: String,
    val act: Int,
    val element: String,
    val color: Color,
    val quote: String = ""
) {
    val auraColor: Color get() = color
}

val allPhoenixStages = listOf(
    PhoenixStage(1, "Sacred Egg Awakening", "The primordial ember stirs within the egg", 1, "🔥 Ember", Color(0xFFFA8000), "Life awakens!"),
    PhoenixStage(2, "First Hatching", "Shell cracks under living celestial fire", 1, "🔥 Flame", Color(0xFFFA8000), "I break free!"),
    PhoenixStage(3, "Baby Fledgling", "Curious golden eyes greeting the dawn", 1, "✨ Spark", AmberGold, "Chirp! Hello world!"),
    PhoenixStage(4, "First Feathers", "Plumage gleams with vibrant crimson & gold", 1, "🪶 Plumage", Color(0xFFFA8000), "Feeling stronger!"),
    PhoenixStage(5, "Nestbound Leap", "Stretching wings at the brink of the cliff", 1, "💨 Breeze", AmberGold, "Ready to leap!"),
    PhoenixStage(6, "First Flight", "Soaring gracefully above the clouds", 1, "🌪 Flight", Color(0xFFFA8000), "I can fly!"),
    PhoenixStage(7, "Ancient Perch", "Watching over the sacred forest canopy", 2, "🌿 Nature", DuolingoGreen, "Guardian watch!"),
    PhoenixStage(8, "Valley Glider", "Riding warm thermal drafts across canyons", 2, "⛰ Valley", AmberGold, "Onward to adventure!"),
    PhoenixStage(9, "Storm Trial", "Facing torrential hurricane winds head on", 2, "⚡️ Lightning", DuolingoBlue, "No storm can stop me!"),
    PhoenixStage(10, "Tempest Battle", "Striking through thunderous vortex storms", 2, "⚡️ Thunder", Color(0xFF6366F1), "Unleash the lightning!"),
    PhoenixStage(11, "Fiery Resilience", "Inner core burns hotter than any cold", 2, "🔥 Inferno", Color(0xFFFF4B4B), "Fire burns eternal!"),
    PhoenixStage(12, "Rebirth Dawn", "Ascending purified from the sacred ashes", 2, "✨ Rebirth", Color(0xFFFA8000), "Risen anew!"),
    PhoenixStage(13, "Arcane Awakening", "Mystical violet flames encircle the wings", 3, "🔮 Arcane", Color(0xFFA560E8), "Mystic energy flows!"),
    PhoenixStage(14, "Radiant Crest", "Golden coronal crest bursts with sunlight", 3, "☀️ Radiance", AmberGold, "Shining bright!"),
    PhoenixStage(15, "Elemental Master", "Wielding the 4 fundamental primal forces", 3, "🌀 Elements", Color(0xFF00E5FF), "Elements align!"),
    PhoenixStage(16, "Golden Aegis", "Armored plumage forged in solar furnaces", 3, "🛡 Solar Aegis", AmberGold, "Invincible shield!"),
    PhoenixStage(17, "Solar Ascent", "Diving straight into the core of the Sun", 3, "☀️ Solar Core", Color(0xFFFA8000), "Touching the sun!"),
    PhoenixStage(18, "Eternal Cosmic Phoenix", "Transcendent celestial deity of the stars", 3, "🌌 Cosmos", Color(0xFFA560E8), "Eternal and boundless!")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoenixSanctuaryScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val activeStage by gameManager.activePhoenixStage.collectAsState()
    var selectedAct by remember { mutableIntStateOf(0) } // 0: All, 1: Act 1, 2: Act 2, 3: Act 3
    var previewStage by remember { mutableStateOf<PhoenixStage?>(null) }
    var equippedToast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(equippedToast) {
        if (equippedToast != null) {
            delay(2000)
            equippedToast = null
        }
    }

    val filteredStages = remember(selectedAct) {
        if (selectedAct == 0) allPhoenixStages else allPhoenixStages.filter { it.act == selectedAct }
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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SubtextGray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "PHOENIX SANCTUARY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Hero Stage Header Banner
            val currentStageObj = allPhoenixStages.getOrNull(activeStage - 1) ?: allPhoenixStages.first()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBackground.copy(alpha = 0.85f))
                    .border(1.5.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(AmberGold, Color(0xFFFA8000))))
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getPhoenixDrawableId(activeStage)),
                            contentDescription = currentStageObj.name,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE COMPANION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Text(
                            text = "Stage $activeStage • ${currentStageObj.name}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = currentStageObj.subtitle,
                            fontSize = 11.sp,
                            color = SubtextGray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Act Filter Pills
            val acts = listOf("ALL (18)", "ACT 1: HATCH", "ACT 2: TRIALS", "ACT 3: COSMIC")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(acts.indices.toList()) { idx ->
                    val isSelected = selectedAct == idx
                    val shape = RoundedCornerShape(12.dp)
                    Box(
                        modifier = Modifier
                            .clip(shape)
                            .background(if (isSelected) AmberGold else CardBackground.copy(alpha = 0.7f))
                            .border(1.dp, if (isSelected) AmberGold else InputBorder, shape)
                            .clickable { selectedAct = idx }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = acts[idx],
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.Black else SubtextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 18 Stages Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredStages) { stage ->
                    val isEquipped = stage.id == activeStage
                    val shape = RoundedCornerShape(18.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(CardBackground.copy(alpha = 0.85f))
                            .border(
                                width = if (isEquipped) 2.dp else 1.2.dp,
                                color = if (isEquipped) AmberGold else InputBorder,
                                shape = shape
                            )
                            .clickable { previewStage = stage }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stage Number & Element Tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isEquipped) AmberGold else InputBorder)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "STAGE ${stage.id}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isEquipped) Color.Black else Color.White
                                )
                            }
                            Text(
                                text = stage.element,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = stage.color
                            )
                        }

                        // Artwork with glowing circle
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(stage.color.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getPhoenixDrawableId(stage.id)),
                                contentDescription = stage.name,
                                modifier = Modifier.size(70.dp)
                            )
                        }

                        // Name & Subtitle
                        Text(
                            text = stage.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = stage.subtitle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtextGray,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 13.sp
                        )

                        // Equip Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isEquipped) DuolingoGreen else AmberGold)
                                .clickable {
                                    gameManager.setActivePhoenixStage(stage.id)
                                    equippedToast = "Equipped ${stage.name}!"
                                }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isEquipped) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "EQUIPPED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "EQUIP",
                                        fontSize = 11.sp,
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

        // Stage Detail 360 Turntable Bottom Sheet
        if (previewStage != null) {
            val stage = previewStage!!
            val isEquipped = stage.id == activeStage
            ModalBottomSheet(
                onDismissRequest = { previewStage = null },
                containerColor = DarkBackground
            ) {
                PhoenixStageDetailSheet(
                    stage = stage,
                    isEquipped = isEquipped,
                    onEquip = {
                        gameManager.setActivePhoenixStage(stage.id)
                        equippedToast = "Equipped ${stage.name}!"
                        previewStage = null
                    }
                )
            }
        }

        // Floating Equip Toast
        AnimatedVisibility(
            visible = equippedToast != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.5.dp, AmberGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🔥", fontSize = 16.sp)
                    Text(
                        text = equippedToast ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoenixStageDetailSheet(
    stage: PhoenixStage,
    isEquipped: Boolean,
    onEquip: () -> Unit
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var sparkOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            sparkOffset = if (sparkOffset == 0f) 1f else 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberGold)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "STAGE ${stage.id} OF 18",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(stage.color.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stage.element,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = stage.color
                )
            }
        }

        // 360 Interactive Turntable
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(stage.color.copy(alpha = 0.15f))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        rotationAngle += dragAmount.x * 0.6f
                    }
                }
                .clickable {
                    rotationAngle += 45f
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getPhoenixDrawableId(stage.id)),
                contentDescription = stage.name,
                modifier = Modifier
                    .size(170.dp)
                    .rotate(rotationAngle)
            )
        }

        Text(
            text = "Drag or tap mascot to spin 360°",
            fontSize = 11.sp,
            color = SubtextGray
        )

        // Stage Name & Description
        Text(
            text = stage.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = stage.subtitle,
            fontSize = 13.sp,
            color = SubtextGray,
            textAlign = TextAlign.Center
        )

        // Quote Box
        if (stage.quote.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(stage.color.copy(alpha = 0.12f))
                    .border(1.dp, stage.color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "“${stage.quote}”",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Equip 3D Button
        Duolingo3DButton(
            title = if (isEquipped) "ACTIVE COMPANION EQUIPPED" else "EQUIP THIS COMPANION",
            onClick = { onEquip() },
            style = if (isEquipped) Duolingo3DButtonStyle.GREEN else Duolingo3DButtonStyle.AMBER,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
