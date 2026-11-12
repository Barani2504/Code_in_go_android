package com.simats.codeingo.ui.exercise.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.MascotView
import com.simats.codeingo.ui.mascot.PhoenixStage
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

data class MiniLessonCardData(
    val title: String,
    val conceptHeadline: String,
    val diagramEmoji: String,
    val shortExplanation: String,
    val securityTakeaway: String
)

@Composable
fun MiniLessonIntroCards(
    unitId: Int,
    levelNumber: Int,
    cards: List<MiniLessonCardData>,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { cards.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicColors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSkip) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Skip intro",
                        tint = dynamicColors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) AmberGold.copy(alpha = 0.15f) else Color(0xFFFEF3C7))
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LEARN • CHAPTER $unitId NODE $levelNumber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        color = if (isDark) AmberGold else Color(0xFFB45309)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Pager Cards
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) { page ->
                val card = cards.getOrElse(page) { cards.first() }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mascot Delivery
                    MascotView(
                        stage = PhoenixStage.FLEDGLING,
                        emotion = MascotEmotion.CURIOUS,
                        size = 110.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = 20.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = card.diagramEmoji,
                                fontSize = 42.sp
                            )
                            Text(
                                text = card.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = AppFontFamily,
                                color = dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = card.conceptHeadline,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) AmberGold else Color(0xFFB45309),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = card.shortExplanation,
                                fontSize = 13.sp,
                                color = dynamicColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(dynamicColors.inputBackground)
                                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "💡 Takeaway: ${card.securityTakeaway}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Pager Indicators & Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Liquid Indicator Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in cards.indices) {
                        val isSelected = i == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // CTA Button
                val isLastPage = pagerState.currentPage == cards.size - 1
                AppButton(
                    title = if (isLastPage) "LET'S DEFEND ⚔️" else "NEXT IDEA →",
                    style = AppButtonStyle.PRIMARY_AMBER,
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            // Can advance to next page or complete
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}

