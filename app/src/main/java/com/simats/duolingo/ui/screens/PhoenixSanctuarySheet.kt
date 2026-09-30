package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.PhoenixStageData
import com.simats.duolingo.data.allPhoenixStages
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PhoenixSanctuarySheet(
    onDismiss: () -> Unit,
) {
    var selectedAct by remember { mutableIntStateOf(0) } // 0: All, 1: Act 1, 2: Act 2, 3: Act 3
    var previewStage by remember { mutableStateOf<PhoenixStageData?>(null) }
    var equippedToast by remember { mutableStateOf<String?>(null) }
    // Evolution cinematic state
    var evolutionFromStage by remember { mutableStateOf<PhoenixStageData?>(null) }
    var evolutionToStage by remember { mutableStateOf<PhoenixStageData?>(null) }
    var showEvolutionCinematic by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val filteredStages = remember(selectedAct) {
        if (selectedAct == 0) allPhoenixStages else allPhoenixStages.filter { it.act == selectedAct }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Navigation & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PHOENIX SANCTUARY",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss) {
                    Text("✕", color = DuolingoSubtext, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Header Section with currently equipped companion
            val currentActive = AppState.activePhoenix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DuolingoHeaderBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF9600), Color(0xFFFF4B4B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = currentActive.drawableResId),
                        contentDescription = currentActive.name,
                        modifier = Modifier.size(42.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "COMPANION MASCOT",
                        color = Color.White.copy(0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Active: Stage ${currentActive.id} â€¢ ${currentActive.name}",
                        color = Color(0xFFFF9600),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Act Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "ALL (18)",
                    1 to "ACT 1: HATCH",
                    2 to "ACT 2: TRIALS",
                    3 to "ACT 3: COSMIC"
                ).forEach { (act, title) ->
                    val isSelected = selectedAct == act
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFFF9600) else DuolingoCardBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFFF9600) else DuolingoInputBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedAct = act }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else DuolingoSubtext,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Grid of Stages
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredStages) { stage ->
                    val isEquipped = stage.id == AppState.activePhoenixStage
                    StageCard(
                        stage = stage,
                        isEquipped = isEquipped,
                        onPreview = { previewStage = stage },
                        onEquip = {
                            AppState.activePhoenixStage = stage.id
                            equippedToast = "Equipped ${stage.name}!"
                            coroutineScope.launch {
                                delay(2000)
                                equippedToast = null
                            }
                        }
                    )
                }
            }
        }

        // Detail overlay using upgraded PhoenixStageDetailOverlay from PhoenixEvolution.kt
        previewStage?.let { stage ->
            val nextStage = allPhoenixStages.firstOrNull { it.id == stage.id + 1 }
            PhoenixStageDetailOverlay(
                stage = stage,
                isEquipped = stage.id == AppState.activePhoenixStage,
                onDismiss = { previewStage = null },
                onEquip = {
                    AppState.activePhoenixStage = stage.id
                    previewStage = null
                    equippedToast = "ðŸ”¥ Equipped ${stage.name}!"
                    coroutineScope.launch {
                        delay(2200)
                        equippedToast = null
                    }
                },
                onEvolve = if (nextStage != null) ({
                    evolutionFromStage = stage
                    evolutionToStage = nextStage
                    previewStage = null
                    showEvolutionCinematic = true
                }) else null
            )
        }

        // Phoenix Evolution Cinematic Overlay
        if (showEvolutionCinematic) {
            val fromS = evolutionFromStage
            val toS = evolutionToStage
            if (fromS != null && toS != null) {
                PhoenixEvolutionCinematic(
                    fromStage = fromS,
                    toStage = toS,
                    onComplete = {
                        AppState.activePhoenixStage = toS.id
                        showEvolutionCinematic = false
                        equippedToast = "âœ¨ Evolved to ${toS.name}!"
                        coroutineScope.launch {
                            delay(2500)
                            equippedToast = null
                        }
                    }
                )
            }
        }

        // Toast notification
        AnimatedVisibility(
            visible = equippedToast != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            equippedToast?.let { toast ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg)
                        .border(1.5.dp, Color(0xFFFF9600), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ðŸ”¥", fontSize = 16.sp)
                    Text(
                        text = toast,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StageCard(
    stage: PhoenixStageData,
    isEquipped: Boolean,
    onPreview: () -> Unit,
    onEquip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(
                width = if (isEquipped) 2.dp else 1.dp,
                color = if (isEquipped) Color(0xFFFF9600) else DuolingoInputBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Stage tag + Element pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isEquipped) Color(0xFFFF9600) else DuolingoInputBorder)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "STAGE ${stage.id}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = stage.element,
                color = stage.auraColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Mascot Character Image
        Box(
            modifier = Modifier
                .size(90.dp)
                .clickable(onClick = onPreview),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(stage.auraColor.copy(alpha = 0.2f))
                    .blur(6.dp)
            )
            Image(
                painter = painterResource(id = stage.drawableResId),
                contentDescription = stage.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Name & Description
        Text(
            text = stage.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stage.subtitle,
            color = DuolingoSubtext,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(26.dp)
        )

        // Equip Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isEquipped) DuolingoGreen else Color(0xFFFF9600))
                .clickable(onClick = onEquip)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(if (isEquipped) "âœ“" else "ðŸ”¥", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (isEquipped) "EQUIPPED" else "EQUIP",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
