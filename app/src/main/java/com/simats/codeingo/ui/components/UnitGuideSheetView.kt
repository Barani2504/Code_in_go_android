package com.simats.codeingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import com.simats.codeingo.data.model.UnitModel
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoOrange
import com.simats.codeingo.ui.theme.DuolingoSubtext
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitGuideSheetView(
    unit: UnitModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📖 Unit Guide",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubtextGray
                    )
                    Text(
                        text = unit.titleDefault,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SubtextGray
                    )
                }
            }

            // "What You'll Learn" Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoCardBg)
                    .border(1.2.dp, InputBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "WHAT YOU'LL LEARN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
                Text(
                    text = "In this unit, you will practice data structure challenges. Prepare to master:",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = dsaTopicsPreview(unit.id),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuolingoGreen,
                        lineHeight = 22.sp
                    )
                }
            }

            // "Rewards System" Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoCardBg)
                    .border(1.2.dp, InputBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "REWARDS SYSTEM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoOrange
                )

                // Candy Crush Golden Stars
                CandyCrushStarsView(earnedStars = 5)

                // Info Rows
                GuideInfoRow(
                    emoji = "⭐",
                    title = "Stars",
                    description = "Answer 10 questions correctly to earn a perfect 5 stars!"
                )
                GuideInfoRow(
                    emoji = "🎯",
                    title = "Accuracy",
                    description = "Accuracy perfectly matches your stars (5 stars = 100%)."
                )
                GuideInfoRow(
                    emoji = "🔥",
                    title = "XP Scaling",
                    description = "Higher units give exponentially more Base XP and Boss XP!"
                )
            }

            // Dismiss Button
            DuolingoButton(
                text = "GOT IT!",
                faceColor = DuolingoBlue,
                shadowColor = DuolingoBlueDark,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GuideInfoRow(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = SubtextGray
            )
        }
    }
}

private fun dsaTopicsPreview(unitId: Int): String {
    return when (unitId) {
        1 -> "• Big-O Notation\n• Basic Syntax\n• Problem Solving"
        2 -> "• Arrays & Strings\n• Two Pointers\n• Sliding Window"
        3 -> "• String Manipulation\n• Palindromes\n• Anagrams"
        4 -> "• Singly Linked Lists\n• Doubly Linked Lists\n• Fast/Slow Pointers"
        5 -> "• LIFO Structures\n• Valid Parentheses\n• Monotonic Stacks"
        6 -> "• FIFO Structures\n• BFS Fundamentals\n• Deque"
        7 -> "• Hash Maps\n• Hash Sets\n• Collision Resolution"
        8 -> "• Binary Trees\n• DFS & BFS Traversals\n• BST"
        9 -> "• Prefix Trees\n• Autocomplete\n• Word Search"
        10 -> "• Graph Traversals\n• Shortest Path\n• Topological Sort"
        11 -> "• Heaps, Tries & DP\n• Hard Problems\n• Final Mastery"
        else -> "• Core Fundamentals\n• Algorithm Design"
    }
}
