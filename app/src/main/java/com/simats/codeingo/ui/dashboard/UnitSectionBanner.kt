package com.simats.codeingo.ui.dashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.UnitModel

/**
 * UnitSectionBanner — DSA Chapter Header Banner Card with Lore Era Tag & Guidebook Button.
 * Exact parity with iOS MainDashboardView.swift unitSectionView.
 */
@Composable
fun UnitSectionBanner(
    unit: UnitModel,
    isUnlocked: Boolean,
    onGuidebookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eraLore = when (unit.id) {
        1 -> "Primordial Ember Era"
        2 -> "The Celestial Chain"
        3 -> "Solar Peak Ascent"
        4 -> "Astral Wings Cycle"
        5 -> "Immortal Sovereign Reign"
        else -> "Phoenix Realm ${unit.unitNumber}"
    }

    val topicsPreview = when (unit.id) {
        1 -> "Contiguous Blocks • Dynamic Arrays • Pointer Math • Vector Registers"
        2 -> "Pointer Traversal • Doubly Linked • Cycle Detection • Node Insertion"
        3 -> "LIFO Architecture • Solar Call Stack • Expression Parsing • Reversals"
        4 -> "FIFO Dispatcher • Ring Buffers • Breadth Traversals • Sliding Window"
        5 -> "Root Ancestry • BST Invariants • Subtree Rotations • Sovereign Traversal"
        else -> "Data Structures & Algorithmic Principles"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (isUnlocked) {
            // Unlocked Chapter Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                unit.themeColor.copy(alpha = 0.42f),
                                unit.themeDarkColor.copy(alpha = 0.35f),
                                Color(0xFF0A1020).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.48f),
                                unit.themeColor.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Phoenix Era Lore Tag
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(0.9.dp, Color.White.copy(alpha = 0.30f), CircleShape)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = eraLore.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }

                    Text(
                        text = unit.titleDefault,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = topicsPreview,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.88f),
                        lineHeight = 16.sp
                    )
                }

                // Guide Button
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(
                            1.1.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.15f))
                            ),
                            CircleShape
                        )
                        .clickable { onGuidebookClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Guide",
                        tint = unit.themeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Guide",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        } else {
            // Locked Chapter Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0F1420).copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(22.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = eraLore.uppercase(),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.55f)
                    )

                    Text(
                        text = unit.titleDefault,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.65f)
                    )

                    Text(
                        text = "🔒 Complete Unit ${unit.unitNumber - 1} to unlock this world!",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.50f)
                    )
                }
            }
        }
    }
}
