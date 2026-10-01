package com.simats.codeingo.ui.phoenix

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.simats.codeingo.R
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

fun getPhoenixDrawableId(stageId: Int): Int {
    return when (stageId) {
        1 -> R.drawable.phoenix_1
        2 -> R.drawable.phoenix_2
        3 -> R.drawable.phoenix_3
        4 -> R.drawable.phoenix_4
        5 -> R.drawable.phoenix_5
        6 -> R.drawable.phoenix_6
        7 -> R.drawable.phoenix_7
        8 -> R.drawable.phoenix_8
        9 -> R.drawable.phoenix_9
        10 -> R.drawable.phoenix_10
        11 -> R.drawable.phoenix_11
        12 -> R.drawable.phoenix_12
        13 -> R.drawable.phoenix_13
        14 -> R.drawable.phoenix_14
        15 -> R.drawable.phoenix_15
        16 -> R.drawable.phoenix_16
        17 -> R.drawable.phoenix_17
        18 -> R.drawable.phoenix_18
        else -> R.drawable.phoenix
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
)

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

@Composable
fun PhoenixSanctuaryScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val activeStage by gameManager.activePhoenixStage.collectAsState()
    var selectedAct by remember { mutableIntStateOf(0) } // 0: All, 1: Act 1, 2: Act 2, 3: Act 3

    val filteredStages = remember(selectedAct) {
        if (selectedAct == 0) allPhoenixStages else allPhoenixStages.filter { it.act == selectedAct }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SubtextGray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PHOENIX SANCTUARY",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Hero Stage Info
        val currentStageObj = allPhoenixStages.getOrNull(activeStage - 1) ?: allPhoenixStages.first()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFA8000).copy(alpha = 0.2f))
                    .border(2.dp, AmberGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getPhoenixDrawableId(activeStage)),
                    contentDescription = currentStageObj.name,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Stage $activeStage: ${currentStageObj.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = currentStageObj.subtitle,
                fontSize = 13.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Act Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val acts = listOf("All (18)", "Act 1", "Act 2", "Act 3")
            acts.forEachIndexed { idx, label ->
                val isSelected = selectedAct == idx
                val shape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(if (isSelected) DuolingoBlue else CardBackground)
                        .clickable { selectedAct = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else SubtextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 18 Stages Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            items(filteredStages) { stage ->
                val isUnlocked = stage.id <= activeStage
                val isCurrent = stage.id == activeStage
                val shape = RoundedCornerShape(16.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(CardBackground)
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = when {
                                isCurrent -> AmberGold
                                isUnlocked -> stage.color.copy(alpha = 0.5f)
                                else -> InputBorder
                            },
                            shape = shape
                        )
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) stage.color.copy(alpha = 0.2f) else Color(0xFF142028)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Image(
                                painter = painterResource(id = getPhoenixDrawableId(stage.id)),
                                contentDescription = stage.name,
                                modifier = Modifier.size(42.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = SubtextGray.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Stage ${stage.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUnlocked) stage.color else SubtextGray
                    )

                    Text(
                        text = stage.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else SubtextGray,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stage.element,
                        fontSize = 10.sp,
                        color = SubtextGray
                    )
                }
            }
        }
    }
}
