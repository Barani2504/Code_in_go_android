package com.simats.duolingo.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.allPhoenixStages
import com.simats.duolingo.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ================================================================== */
/*  GAME CONSTANTS & METRICS                                          */
/* ================================================================== */

const val CHAPTER_COUNT = 5
const val LEVELS_PER_CHAPTER = 7
const val MAX_LEVEL = CHAPTER_COUNT * LEVELS_PER_CHAPTER // 35
const val LOSSES_TO_DEATH = 3
const val WINS_TO_ASCEND = 5

val chapterNames = listOf(
    "Syntax Sanctum", "Loop Labyrinth", "Function Forge",
    "Data Dominion", "Algorithm Apex"
)

val defaultStageNames = listOf(
    "Mystic Egg", "Hairline Crack", "Shaking Egg", "Hatching!",
    "Ember Chick", "Fluffy Flameling", "Spark Fledgling", "Ash Wing",
    "Cinder Glider", "Ember Flyer", "Flame Sprite", "Blaze Wing",
    "Inferno Youngling", "Sunfire Hawk", "Radiant Firebird", "Solar Phoenix",
    "Eternal Ember", "PHOENIX ASCENDANT"
)

fun stageForLevel(level: Int): Int =
    (1 + (level - 1) * (MAX_STAGE - 1) / (MAX_LEVEL - 1)).coerceIn(1, MAX_STAGE)

/* ================================================================== */
/*  PHOENIX MODEL & RULES                                             */
/* ================================================================== */

enum class PhoenixEvent { VICTORY, EVOLVE, ASCEND, HURT, DEATH, REBIRTH }

data class PhoenixSave(
    val level: Int = 1,
    val stage: Int = 1,
    val winStreak: Int = 0,
    val lossStreak: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val isAshes: Boolean = false,
    val damage: Float = 0f,
    val ascendant: Boolean = false
) {
    val chapter: Int get() = ((level - 1) / LEVELS_PER_CHAPTER).coerceIn(0, CHAPTER_COUNT - 1)
    val levelInChapter: Int get() = ((level - 1) % LEVELS_PER_CHAPTER) + 1
    val progress: Float get() = (level - 1f) / (MAX_LEVEL - 1f)
}

val PhoenixSaveSaver = listSaver<PhoenixSave, Any>(
    save = {
        listOf(
            it.level, it.stage, it.winStreak, it.lossStreak, it.totalWins,
            it.totalLosses, it.isAshes, it.damage, it.ascendant
        )
    },
    restore = {
        PhoenixSave(
            level = it[0] as Int, stage = it[1] as Int,
            winStreak = it[2] as Int, lossStreak = it[3] as Int,
            totalWins = it[4] as Int, totalLosses = it[5] as Int,
            isAshes = it[6] as Boolean, damage = it[7] as Float,
            ascendant = it[8] as Boolean
        )
    }
)

fun PhoenixSave.afterWin(): Pair<PhoenixSave, PhoenixEvent> {
    val wasAshes = isAshes
    val newLevel = (level + 1).coerceAtMost(MAX_LEVEL)
    val newStreak = if (wasAshes) 1 else winStreak + 1
    val newStage =
        if (wasAshes) (stage - 1).coerceAtLeast(1)
        else maxOf(stage, stageForLevel(newLevel))
    val newAscendant = newStreak >= WINS_TO_ASCEND

    val next = copy(
        level = newLevel,
        stage = newStage,
        winStreak = newStreak,
        lossStreak = 0,
        totalWins = totalWins + 1,
        isAshes = false,
        damage = (damage - 0.5f).coerceAtLeast(0f),
        ascendant = newAscendant
    )
    val event = when {
        wasAshes -> PhoenixEvent.REBIRTH
        newAscendant && !ascendant -> PhoenixEvent.ASCEND
        newStage > stage -> PhoenixEvent.EVOLVE
        else -> PhoenixEvent.VICTORY
    }
    return next to event
}

fun PhoenixSave.afterLose(): Pair<PhoenixSave, PhoenixEvent> {
    val newLossStreak = lossStreak + 1
    val dead = newLossStreak >= LOSSES_TO_DEATH
    val next = copy(
        level = (level - 1).coerceAtLeast(1),
        winStreak = 0,
        lossStreak = newLossStreak,
        totalLosses = totalLosses + 1,
        isAshes = dead,
        damage = if (dead) 1f else (damage + 0.34f).coerceAtMost(1f),
        ascendant = false
    )
    return next to if (dead) PhoenixEvent.DEATH else PhoenixEvent.HURT
}

object PhoenixRules {
    fun win(save: PhoenixSave): Pair<PhoenixSave, PhoenixEvent> = save.afterWin()
    fun lose(save: PhoenixSave): Pair<PhoenixSave, PhoenixEvent> = save.afterLose()
}

/* ================================================================== */
/*  HUD PIECES                                                        */
/* ================================================================== */

@Composable
private fun TopHud(
    save: PhoenixSave,
    onDismiss: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Close or Back Button if provided
        if (onDismiss != null) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            ) {
                Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        // Chapter pill
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0x22FFFFFF))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                "CH ${save.chapter + 1} · ${chapterNames[save.chapter]}",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        Spacer(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Streak flame
            if (save.winStreak > 0) {
                StreakChip(save.winStreak)
                Spacer(Modifier.width(8.dp))
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (save.isAshes) Color(0x33FF1744) else Color(0x22FFC107)
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    "Lv ${save.level}",
                    color = if (save.isAshes) Color(0xFFFF8A80) else EmberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun StreakChip(streak: Int) {
    val inf = rememberInfiniteTransition(label = "streak")
    val pulse by inf.animateFloat(
        initialValue = 0.9f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val hot = streak >= WINS_TO_ASCEND
    Box(
        Modifier
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
            .clip(RoundedCornerShape(50))
            .background(
                if (hot) Brush.horizontalGradient(listOf(EmberGold, EmberCrimson))
                else Brush.horizontalGradient(listOf(EmberOrange, EmberGold))
            )
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(
            "🔥 $streak",
            color = Color(0xFF2B1A00),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ChapterProgress(save: PhoenixSave, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(CHAPTER_COUNT) { ch ->
            val startLevel = ch * LEVELS_PER_CHAPTER + 1
            val done = (save.level - startLevel + 1).coerceIn(0, LEVELS_PER_CHAPTER).toFloat()
            val frac = done / LEVELS_PER_CHAPTER
            Box(
                Modifier
                    .weight(1f)
                    .height(9.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x22FFFFFF))
            ) {
                if (frac > 0.001f) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(frac)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (frac >= 1f) Brush.horizontalGradient(
                                    listOf(MintGreen, Color(0xFF89E219))
                                )
                                else Brush.horizontalGradient(
                                    listOf(EmberOrange, EmberGold)
                                )
                            )
                    )
                }
            }
        }
    }
}

/* ================================================================== */
/*  MAIN INTERACTIVE EVOLUTION SCREEN                                 */
/* ================================================================== */

@Composable
fun PhoenixEvolutionScreen(
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    /* ---------- Persistent Game State ---------- */
    var save by rememberSaveable(stateSaver = PhoenixSaveSaver) {
        mutableStateOf(
            PhoenixSave(
                level = (AppState.activeLevelIndex).coerceIn(1, MAX_LEVEL),
                stage = AppState.activePhoenixStage.coerceIn(1, MAX_STAGE),
                winStreak = AppState.dayStreak
            )
        )
    }

    /* ---------- Animation Drivers ---------- */
    val stageAnim = remember { Animatable(save.stage.toFloat()) }
    val burst = remember { Animatable(0f) }
    val bannerT = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }
    val shake = remember { Animatable(0f) }
    val squash = remember { Animatable(1f) }
    val burn = remember { Animatable(if (save.isAshes) 1f else 0f) }
    val hurtPulse = remember { Animatable(0f) }
    val rise = remember { Animatable(1f) }
    val fly = remember { Animatable(0f) }

    var burstMode by remember { mutableStateOf(BurstMode.VICTORY) }
    var flashColor by remember { mutableStateOf(EmberGold) }
    var banner by remember { mutableStateOf("") }
    var bannerColor by remember { mutableStateOf(EmberGold) }

    val scope = rememberCoroutineScope()

    /* ---------- Sync on first composition ---------- */
    LaunchedEffect(save.stage) {
        stageAnim.animateTo(save.stage.toFloat(), tween(400))
    }

    /* =================== WIN CINEMATIC =================== */
    suspend fun playWin(event: PhoenixEvent) {
        banner = when (event) {
            PhoenixEvent.REBIRTH -> "REBORN!"
            PhoenixEvent.ASCEND -> "ASCENDED!"
            PhoenixEvent.EVOLVE -> "EVOLVED!"
            else -> "VICTORY!"
        }
        bannerColor = when (event) {
            PhoenixEvent.REBIRTH -> Color(0xFFFFF59D)
            PhoenixEvent.ASCEND -> EmberCrimson
            else -> EmberGold
        }

        // Banner pop
        scope.launch {
            bannerT.snapTo(0f)
            bannerT.animateTo(1f, tween(1500, easing = LinearEasing))
        }

        // Screen shake
        scope.launch {
            shake.snapTo(if (event == PhoenixEvent.ASCEND) 1.6f else 1f)
            shake.animateTo(
                0f,
                spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium)
            )
        }

        if (event == PhoenixEvent.REBIRTH) {
            burstMode = BurstMode.REBIRTH
            flashColor = Color.White

            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(1f, tween(140))
                flash.animateTo(0f, tween(800))
            }
            scope.launch {
                burn.snapTo(1f)
                delay(180)
                burn.animateTo(0f, tween(950, easing = FastOutSlowInEasing))
            }
            scope.launch {
                rise.snapTo(0f)
                rise.animateTo(1f, tween(1150, easing = FastOutSlowInEasing))
            }
            scope.launch {
                burst.snapTo(0f)
                delay(260)
                burst.animateTo(1f, tween(1700, easing = LinearEasing))
                burst.snapTo(0f)
            }
            hurtPulse.animateTo(0f, tween(300))
            stageAnim.snapTo(save.stage.toFloat())
        } else {
            burstMode = BurstMode.VICTORY
            flashColor = if (event == PhoenixEvent.ASCEND) Color.White else EmberGold

            // Anticipation squash → bouncy release
            scope.launch {
                squash.snapTo(0.72f)
                squash.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow)
                )
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(if (event == PhoenixEvent.ASCEND) 0.85f else 0.5f, tween(120))
                flash.animateTo(0f, tween(650))
            }
            scope.launch {
                burst.snapTo(0f)
                delay(160)
                burst.animateTo(
                    1f,
                    tween(if (event == PhoenixEvent.ASCEND) 2000 else 1250, easing = LinearEasing)
                )
                burst.snapTo(0f)
            }
            // Little victory hop
            scope.launch {
                fly.snapTo(0f)
                fly.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
                fly.animateTo(0f, tween(620, easing = FastOutLinearInEasing))
            }
            // Morph stage
            if (event == PhoenixEvent.EVOLVE || event == PhoenixEvent.ASCEND) {
                scope.launch {
                    delay(200)
                    stageAnim.animateTo(
                        save.stage.toFloat(),
                        tween(1000, easing = FastOutSlowInEasing)
                    )
                }
            } else {
                scope.launch { stageAnim.animateTo(save.stage.toFloat(), tween(400)) }
            }
            hurtPulse.animateTo(0f, tween(400))
        }
    }

    /* =================== LOSE CINEMATIC =================== */
    suspend fun playLose(event: PhoenixEvent) {
        shake.snapTo(if (event == PhoenixEvent.DEATH) 2.2f else 1.2f)

        if (event == PhoenixEvent.DEATH) {
            banner = "ASHES..."
            bannerColor = Color(0xFFFF8A80)
            burstMode = BurstMode.DEATH
            flashColor = EmberCrimson

            scope.launch {
                bannerT.snapTo(0f)
                bannerT.animateTo(1f, tween(2200, easing = LinearEasing))
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(0.95f, tween(170))
                flash.animateTo(0f, tween(1000))
            }
            scope.launch {
                shake.animateTo(
                    0f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                )
            }
            scope.launch {
                hurtPulse.snapTo(1f)
                hurtPulse.animateTo(0f, tween(700))
            }
            scope.launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(1000, easing = LinearEasing))
                burst.snapTo(0f)
            }
            scope.launch {
                squash.snapTo(1.14f)
                squash.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            }
            delay(260)
            burn.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
            hurtPulse.snapTo(0f)
        } else {
            banner = "OUCH!"
            bannerColor = Color(0xFFFF8A80)
            burstMode = BurstMode.HURT
            flashColor = EmberCrimson

            scope.launch {
                bannerT.snapTo(0f)
                bannerT.animateTo(1f, tween(1100, easing = LinearEasing))
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(0.45f, tween(110))
                flash.animateTo(0f, tween(520))
            }
            scope.launch {
                shake.animateTo(
                    0f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
                )
            }
            scope.launch {
                hurtPulse.snapTo(1f)
                hurtPulse.animateTo(0f, tween(950, easing = LinearEasing))
            }
            scope.launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(720, easing = LinearEasing))
                burst.snapTo(0f)
            }
            scope.launch {
                squash.snapTo(1.16f)
                squash.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium)
                )
            }
        }
    }

    /* =================== HANDLERS =================== */
    fun onWin() {
        val (next, event) = save.afterWin()
        save = next
        AppState.activePhoenixStage = next.stage
        AppState.dayStreak = next.winStreak
        scope.launch { playWin(event) }
    }

    fun onLose() {
        val (next, event) = save.afterLose()
        save = next
        AppState.loseHeart()
        scope.launch { playLose(event) }
    }

    // Direct stage morph test
    fun setStage(st: Int) {
        val clamped = st.coerceIn(1, MAX_STAGE)
        save = save.copy(stage = clamped, isAshes = false, damage = 0f)
        AppState.activePhoenixStage = clamped
        scope.launch {
            stageAnim.animateTo(clamped.toFloat(), tween(600, easing = FastOutSlowInEasing))
        }
    }

    /* =================== UI =================== */
    Box(modifier.fillMaxSize()) {
        EmberBackground(Modifier.fillMaxSize())

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopHud(save = save, onDismiss = onDismiss)

            Spacer(Modifier.height(4.dp))

            /* ---------- CREATURE STAGE ---------- */
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        translationX = shake.value * 22f
                        translationY = -fly.value * 46f
                    },
                contentAlignment = Alignment.Center
            ) {
                PhoenixCreature(
                    stage = stageAnim.value,
                    damage = save.damage,
                    burn = burn.value,
                    hurtPulse = hurtPulse.value,
                    rise = rise.value,
                    ascendant = save.ascendant,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleY = squash.value
                            scaleX = 2f - squash.value
                        }
                )

                FxOverlay(
                    progress = burst.value,
                    mode = burstMode,
                    modifier = Modifier.fillMaxSize()
                )

                // Full-screen colour flash
                if (flash.value > 0.01f) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(flashColor.copy(alpha = flash.value * 0.55f))
                    )
                }

                // Banner text
                if (bannerT.value > 0f && banner.isNotEmpty()) {
                    val t = bannerT.value
                    Text(
                        banner,
                        color = bannerColor,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .graphicsLayer {
                                val pop =
                                    FastOutSlowInEasing.transform((t * 3.2f).coerceAtMost(1f))
                                scaleX = 0.45f + pop * 0.75f
                                scaleY = 0.45f + pop * 0.75f
                                alpha = (1.5f - t * 1.5f).coerceIn(0f, 1f)
                                translationY = -t * 70f
                                rotationZ = (1f - pop) * -6f
                            }
                    )
                }
            }

            /* ---------- STAGE CHIP SELECTOR (Quick cycle) ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { setStage(save.stage - 1) },
                    enabled = save.stage > 1,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("◀", color = if (save.stage > 1) Color.White else Color.Gray, fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "STAGE ${save.stage} OF 18",
                        color = EmberGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = { setStage(save.stage + 1) },
                    enabled = save.stage < MAX_STAGE,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("▶", color = if (save.stage < MAX_STAGE) Color.White else Color.Gray, fontSize = 16.sp)
                }
            }

            /* ---------- NAME + PROGRESS ---------- */
            val displayName = remember(save.stage, save.isAshes) {
                if (save.isAshes) {
                    "Ashes of the Phoenix"
                } else {
                    allPhoenixStages.firstOrNull { it.id == save.stage }?.name
                        ?: defaultStageNames.getOrElse(save.stage - 1) { "Phoenix Stage ${save.stage}" }
                }
            }

            AnimatedContent(
                targetState = displayName,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn()) togetherWith
                            (slideOutVertically { -it / 2 } + fadeOut())
                },
                label = "name"
            ) { name ->
                Text(
                    text = name,
                    color = if (save.isAshes) Color(0xFFBDBDBD) else Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { save.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                color = MintGreen,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(Modifier.height(8.dp))

            ChapterProgress(save)

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (save.isAshes)
                    "Win a lesson to rise from the ashes"
                else
                    "Level ${save.levelInChapter}/$LEVELS_PER_CHAPTER · Chapter ${save.chapter + 1} of $CHAPTER_COUNT",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- CONTROLS ---------- */
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = ::onLose,
                    enabled = !save.isAshes,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E2A2A),
                        disabledContainerColor = Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        "LOSE / HURT",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = ::onWin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (save.isAshes) EmberGold else MintGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                ) {
                    Text(
                        when {
                            save.isAshes -> "RISE FROM ASHES"
                            save.level >= MAX_LEVEL -> "COMPLETE LESSON"
                            else -> "WIN LESSON"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        color = if (save.isAshes) Color(0xFF2B1A00) else Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
