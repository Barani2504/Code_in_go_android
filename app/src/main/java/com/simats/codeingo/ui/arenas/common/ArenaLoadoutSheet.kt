package com.simats.codeingo.ui.arenas.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

enum class ArenaTacticalTool(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    HINT_LENS(
        title = "Hint Lens",
        subtitle = "Cryptanalytic Vision",
        description = "Highlights target matrix coordinates, bit shifts, or algorithmic hints once per level.",
        icon = Icons.Default.Lightbulb,
        accentColor = AmberGold
    ),
    TIME_FREEZE(
        title = "Time Freeze",
        subtitle = "Clock Drift Inhibitor",
        description = "Pauses boss timers and rapid countdown mechanics for 5 vital seconds.",
        icon = Icons.Default.HourglassBottom,
        accentColor = Color(0xFF00C3FF)
    ),
    SECOND_CHANCE(
        title = "Second Chance",
        subtitle = "Parity Redundancy Shield",
        description = "Absorbs 1 calculation failure or trap collision without forfeiting a life heart.",
        icon = Icons.Default.Shield,
        accentColor = DuolingoGreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaLoadoutSheet(
    selectedTool: ArenaTacticalTool?,
    onSelectTool: (ArenaTacticalTool?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = dynamicColors.cardBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tactical Loadout",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                        Text(
                            text = "Select 1 free tactical support module",
                            fontSize = 12.sp,
                            color = dynamicColors.textSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ArenaTacticalTool.values().forEach { tool ->
                val isSelected = selectedTool == tool
                val borderColor = if (isSelected) tool.accentColor else dynamicColors.cardBorder
                val bgColor = if (isSelected) tool.accentColor.copy(alpha = 0.12f) else dynamicColors.cardBackground.copy(alpha = 0.5f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            if (isSelected) onSelectTool(null) else onSelectTool(tool)
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tool.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = tool.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tool.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• " + tool.subtitle,
                                fontSize = 11.sp,
                                color = tool.accentColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = tool.description,
                            fontSize = 12.sp,
                            color = dynamicColors.textSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) tool.accentColor else Color.Transparent)
                            .border(1.5.dp, if (isSelected) tool.accentColor else dynamicColors.cardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Equipped",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Duolingo3DButton(
                title = if (selectedTool != null) "Equip ${selectedTool.title}" else "Proceed Without Tool",
                style = Duolingo3DButtonColor.GREEN,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
