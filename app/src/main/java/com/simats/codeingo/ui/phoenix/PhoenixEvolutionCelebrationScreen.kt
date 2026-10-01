package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun PhoenixEvolutionCelebrationScreen(
    fromStageId: Int,
    toStageId: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fromStage = allPhoenixStages.getOrNull(fromStageId - 1) ?: allPhoenixStages.first()
    val toStage = allPhoenixStages.getOrNull(toStageId - 1) ?: allPhoenixStages.last()

    val transition = rememberInfiniteTransition(label = "AuraPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF381504), DarkBackground),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PHOENIX EVOLUTION!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Evolution Visual (From -> To)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // From Stage
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getPhoenixDrawableId(fromStageId)),
                            contentDescription = "Stage $fromStageId",
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Stage $fromStageId", fontSize = 12.sp, color = SubtextGray)
                }

                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "➔", fontSize = 28.sp, color = AmberGold)
                Spacer(modifier = Modifier.width(20.dp))

                // To Stage with Pulsing Glow
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0xFFFA8000).copy(alpha = 0.3f))
                        )
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFA8000).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getPhoenixDrawableId(toStageId)),
                                contentDescription = "Stage $toStageId",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Stage $toStageId",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = toStage.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = toStage.subtitle,
                fontSize = 14.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "\"${toStage.quote}\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFA8000),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            DuolingoButton(
                text = "CONTINUE",
                faceColor = DuolingoGreen,
                shadowColor = DuolingoGreenDark,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
