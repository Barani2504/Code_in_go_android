package com.simats.codeingo.ui.phoenix

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import com.simats.codeingo.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.PhoenixEmotion
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

// ══════════════════════════════════════════════════════════════════
// 🦅 PhoenixDynamicLogoView — Dynamic Reactive Mascot Brand Header
// ══════════════════════════════════════════════════════════════════

@Composable
fun PhoenixDynamicLogoView(
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    showSubtitleBadge: Boolean = true,
    size: Dp = 32.dp,
    enableTapSheet: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val equippedCosmetics by com.simats.codeingo.domain.GameManager.instance.equippedCosmetics.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (enableTapSheet) {
                    showSheet = true
                }
                onTap?.invoke()
            }
    ) {
        // Living Mascot Avatar with dynamic aura glow + equipped cosmetic badge
        val effectiveAuraColor = when (equippedCosmetics.auraId) {
            "aura_quantum", "aura_lightning" -> Color(0xFF06B6D4)
            "aura_frost" -> Color(0xFF38BDF8)
            "aura_rainbow" -> Color(0xFFA855F7)
            "aura_sparkle" -> com.simats.codeingo.ui.theme.AmberGold
            else -> currentEmotion.auraColor
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size + 8.dp)
        ) {
            // Pulsing ambient aura
            Box(
                modifier = Modifier
                    .size(size + 6.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(effectiveAuraColor.copy(alpha = 0.35f))
            )

            // Mascot Image
            PhoenixMascotImage(
                emotion = currentEmotion,
                size = size,
                modifier = Modifier.shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = effectiveAuraColor.copy(alpha = 0.55f),
                    spotColor = effectiveAuraColor.copy(alpha = 0.55f)
                )
            )

            if (equippedCosmetics.headwearId != "none_head") {
                val hatEmoji = com.simats.codeingo.ui.shop.CosmeticsCatalog.item(equippedCosmetics.headwearId)?.emoji
                if (hatEmoji != null) {
                    Text(
                        text = hatEmoji,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }

        if (showTitle || showSubtitleBadge) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                if (showTitle) {
                    Text(
                        text = "AshCrypt",
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = if (size > 30.dp) 18.sp else 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        maxLines = 1
                    )
                }

                if (showSubtitleBadge) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(currentEmotion.auraColor.copy(alpha = 0.22f))
                            .border(0.8.dp, currentEmotion.auraColor.copy(alpha = 0.45f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentEmotion.emoji,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = currentEmotion.title,
                            color = currentEmotion.auraColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "•",
                            color = currentEmotion.auraColor.copy(alpha = 0.7f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = currentEmotion.feeling,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.92f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = AppFontFamily,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        PhoenixEmotionPickerSheet(
            onDismiss = { showSheet = false }
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun PhoenixMascotImage(
    emotion: PhoenixEmotion,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resName = "phoenix_emotion_${emotion.id}"
    val resId = remember(emotion.id) {
        val foundId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (foundId != 0) foundId else R.drawable.phoenix_emotion_0
    }

    Image(
        painter = painterResource(id = resId),
        contentDescription = emotion.title,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}
