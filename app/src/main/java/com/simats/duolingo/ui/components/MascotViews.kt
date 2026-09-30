package com.simats.duolingo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.PhoenixStageData
import com.simats.duolingo.data.allPhoenixStages
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ─── 1. Unit 1 Pathfinder Mascot (Phoenix Mascot - Supports all 18 Stages) ─────
@Composable
fun DuoBackpackMascotView(
    onOpenSanctuary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stageData = AppState.activePhoenix
    val auraColor = stageData.auraColor

    var showSpeechBubble by remember { mutableStateOf(false) }
    var tapSpinAngle by remember { mutableFloatStateOf(0f) }
    var starStep by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "phoenixFloat")
    val mascotYOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "yOffset"
    )
    val mascotTilt by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "tilt"
    )
    val auraPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auraScale"
    )

    val pathStars = listOf("🔥", "✨", "🌟", "💫", "⚡️")

    LaunchedEffect(Unit) {
        while (true) {
            delay(1100)
            starStep = (starStep + 1) % pathStars.size
        }
    }

    Box(
        modifier = modifier
            .size(96.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showSpeechBubble = true
                        tapSpinAngle += 360f
                        coroutineScope.launch {
                            delay(1800)
                            showSpeechBubble = false
                        }
                    },
                    onLongPress = {
                        onOpenSanctuary()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Warm Fiery Shadow & Glow beneath Phoenix
        Box(
            modifier = Modifier
                .offset(y = 44.dp)
                .size(width = 84.dp, height = 16.dp)
                .scale(auraPulseScale)
                .clip(CircleShape)
                .background(auraColor.copy(alpha = 0.38f))
        )

        // Floating star / spark
        Text(
            text = pathStars[starStep % pathStars.size],
            fontSize = 16.sp,
            modifier = Modifier
                .offset(x = (-34).dp, y = if (starStep % 2 == 0) (-48).dp else (-30).dp)
                .alpha(if (starStep % 2 == 0) 0.95f else 0.25f)
        )

        // Speech Bubble
        AnimatedVisibility(
            visible = showSpeechBubble,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-62).dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(auraColor, Color(0xFFEB3214))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stageData.name.uppercase(),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                    Text(
                        text = stageData.quote,
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }

        // Phoenix Stage Mascot Artwork
        Image(
            painter = painterResource(id = stageData.drawableResId),
            contentDescription = stageData.name,
            modifier = Modifier
                .size(92.dp)
                .scale(if (showSpeechBubble) 1.15f else 1.0f)
                .graphicsLayer {
                    rotationZ = tapSpinAngle + (if (showSpeechBubble) 0f else mascotTilt)
                    translationY = mascotYOffset
                },
            contentScale = ContentScale.Fit
        )
    }
}

// ─── 2. Unit 2 Lily Purple Character Mascot ──────────────────────────────────
@Composable
fun LilyPurpleMascotView(modifier: Modifier = Modifier) {
    val purpleColor = Color(206, 130, 255)
    var showSpeechBubble by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "lilyAnim")
    val floatingNotesY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "notesY"
    )
    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )

    Box(
        modifier = modifier
            .size(86.dp)
            .clickable {
                showSpeechBubble = true
                coroutineScope.launch {
                    delay(1500)
                    showSpeechBubble = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .offset(y = 40.dp)
                .size(width = 74.dp, height = 14.dp)
                .clip(CircleShape)
                .background(purpleColor.copy(alpha = 0.4f))
        )

        // Musical notes
        Row(
            modifier = Modifier
                .offset(y = (-38).dp)
                .alpha(0.85f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🎵", fontSize = 15.sp, modifier = Modifier.offset(y = (floatingNotesY - 12).dp))
            Text("✨", fontSize = 13.sp, modifier = Modifier.offset(y = (-floatingNotesY).dp))
            Text("🎶", fontSize = 14.sp, modifier = Modifier.offset(y = (floatingNotesY - 6).dp))
        }

        // Speech bubble
        AnimatedVisibility(
            visible = showSpeechBubble,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-54).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(purpleColor)
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text("MORE TRANSPORT?", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // Character Graphic
        Box(
            modifier = Modifier
                .size(76.dp)
                .graphicsLayer { rotationZ = swayAngle },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(purpleColor.copy(alpha = 0.25f))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(89, 51, 140)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(width = 42.dp, height = 26.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(purpleColor)
                )
            }
        }
    }
}

// ─── 3. Unit 3 Vikram & Bees Mascot View ──────────────────────────────────────
@Composable
fun VikramBeesMascotView(modifier: Modifier = Modifier) {
    val tealColor = Color(0, 205, 156)
    var beeAngle by remember { mutableFloatStateOf(0f) }
    var showSpeechBubble by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "leafAnim")
    val leafYOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "leafY"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            beeAngle += 0.08f
        }
    }

    Box(
        modifier = modifier
            .size(88.dp)
            .clickable {
                showSpeechBubble = true
                coroutineScope.launch {
                    delay(1500)
                    showSpeechBubble = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .offset(y = 42.dp)
                .size(width = 78.dp, height = 16.dp)
                .clip(CircleShape)
                .background(tealColor.copy(alpha = 0.35f))
        )

        // Circling Bees (3 bees in 3D orbit)
        for (i in 0 until 3) {
            val angleOffset = i * (2.0 * Math.PI / 3.0)
            val currentAngle = beeAngle + angleOffset
            val xPos = (cos(currentAngle) * 38).toFloat()
            val yPos = (sin(currentAngle) * 18 - 20).toFloat()
            val isFront = sin(currentAngle) > 0

            Text(
                text = "🐝",
                fontSize = 14.sp,
                modifier = Modifier
                    .offset(x = xPos.dp, y = yPos.dp)
                    .scale(if (isFront) 1.15f else 0.85f)
                    .alpha(if (isFront) 1f else 0.6f)
            )
        }

        // Leaves
        Row(
            modifier = Modifier
                .offset(y = (-38).dp)
                .alpha(0.85f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🌿", fontSize = 13.sp, modifier = Modifier.offset(y = leafYOffset.dp))
            Text("🍃", fontSize = 11.sp, modifier = Modifier.offset(y = (-leafYOffset).dp))
        }

        // Speech Bubble
        AnimatedVisibility(
            visible = showSpeechBubble,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-54).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(tealColor)
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text("RESERVED A ROOM!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // Character Face & Turban
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tealColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).border(1.5.dp, Color.White, CircleShape))
                        Box(modifier = Modifier.size(8.dp).border(1.5.dp, Color.White, CircleShape))
                    }
                    Box(modifier = Modifier.size(width = 18.dp, height = 8.dp).clip(CircleShape).background(Color(51, 51, 51)))
                }
            }
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 22.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tealColor.copy(alpha = 0.85f))
            )
        }
    }
}

// ─── 4. Unit 4 Oscar Artist Mascot View ──────────────────────────────────────
@Composable
fun OscarArtistMascotView(modifier: Modifier = Modifier) {
    val orangeColor = Color(255, 150, 0)
    var showSpeechBubble by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "oscarAnim")
    val paletteBounce by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -5f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "paletteBounce"
    )

    Box(
        modifier = modifier
            .size(86.dp)
            .clickable {
                showSpeechBubble = true
                coroutineScope.launch {
                    delay(1500)
                    showSpeechBubble = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .offset(y = 40.dp)
                .size(width = 74.dp, height = 14.dp)
                .clip(CircleShape)
                .background(orangeColor.copy(alpha = 0.35f))
        )

        // Palette & Brush
        Row(
            modifier = Modifier.offset(y = (-38).dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🎨", fontSize = 15.sp, modifier = Modifier.offset(y = (paletteBounce - 6).dp))
            Text("✨", fontSize = 13.sp, modifier = Modifier.offset(y = (-paletteBounce).dp))
            Text("🖌️", fontSize = 14.sp, modifier = Modifier.offset(y = (paletteBounce - 4).dp))
        }

        // Speech Bubble
        AnimatedVisibility(
            visible = showSpeechBubble,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-54).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(orangeColor)
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text("BON APPÉTIT!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // Oscar Character Face
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (paletteBounce * 0.5f).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(230, 178, 102)),
                contentAlignment = Alignment.Center
            ) {
                Text("🥸", fontSize = 22.sp)
            }
            Box(
                modifier = Modifier
                    .size(width = 34.dp, height = 22.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(orangeColor)
            )
        }
    }
}

// ─── 5. Unit 5 Junior Party Mascot View ──────────────────────────────────────
@Composable
fun JuniorPartyMascotView(modifier: Modifier = Modifier) {
    var showSpeechBubble by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "juniorAnim")
    val hopY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hopY"
    )
    val flagRotation by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flagRot"
    )

    Box(
        modifier = modifier
            .size(86.dp)
            .clickable {
                showSpeechBubble = true
                coroutineScope.launch {
                    delay(1500)
                    showSpeechBubble = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .offset(y = 40.dp)
                .size(width = 74.dp, height = 14.dp)
                .clip(CircleShape)
                .background(DuolingoBlue.copy(alpha = 0.35f))
        )

        // Party items
        Row(
            modifier = Modifier.offset(y = (-38).dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("🎉", fontSize = 15.sp, modifier = Modifier.offset(y = (hopY - 4).dp))
            Text("🚩", fontSize = 15.sp, modifier = Modifier.graphicsLayer { rotationZ = flagRotation })
            Text("🎊", fontSize = 13.sp, modifier = Modifier.offset(y = (-hopY).dp))
        }

        // Speech Bubble
        AnimatedVisibility(
            visible = showSpeechBubble,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-54).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DuolingoBlue)
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text("WE DID IT!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // Junior Character Face
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = hopY.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(242, 204, 153)),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.Black))
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.Black))
                }
            }
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DuolingoBlue)
            )
        }
    }
}
