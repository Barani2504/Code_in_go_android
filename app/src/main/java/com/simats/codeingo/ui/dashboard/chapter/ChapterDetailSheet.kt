package com.simats.codeingo.ui.dashboard.chapter

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.CnsChapter
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonSize
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.dashboard.PhoenixEggCompanionMascotView
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.accentContainer
import com.simats.codeingo.ui.theme.border
import com.simats.codeingo.ui.theme.statusSuccess
import com.simats.codeingo.ui.theme.subtle

/**
 * 📜 ChapterDetailSheet — Modal Bottom Sheet displayed when tapping any chapter or node.
 *
 * Implements:
 *  - Chapter title & Realm badge
 *  - One-line promise & 5-minute time estimate
 *  - 3 "You will be able to..." bullets
 *  - 4 parts: [1. Lesson | 2. Lab | 3. Practice | 4. Prove-it]
 *  - Ash mascot hint on *why* this chapter matters
 *  - Big "Start Chapter" / "Continue" button
 *  - Shortcut button into the visualizer lab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailSheet(
    chapter: CnsChapter,
    isCompleted: Boolean,
    onStartLesson: () -> Unit,
    onOpenLab: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val parts = listOf(
        Pair("1. Lesson", "3-4 micro-lessons with analogies"),
        Pair("2. Lab", "${chapter.visualizerTitle} interactive workbench"),
        Pair("3. Practice", "6-8 questions with counter-explanations"),
        Pair("4. Prove-it", "Chapter capstone mastery check")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AshTheme.colors.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header: Chapter Number & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhoenixEggCompanionMascotView(
                    levelNumber = chapter.id * 5,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AshTheme.colors.brand.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CHAPTER ${chapter.number} • ${chapter.realmTitle.uppercase()}",
                                style = AshTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AshTheme.colors.brand,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⏱️ ${chapter.estimatedMinutes} min",
                            style = AshTheme.typography.labelSmall,
                            color = AshTheme.colors.subtle
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chapter.title,
                        style = AshTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.ink,
                        fontSize = 20.sp,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ash Mascot Hint: "Why this chapter matters"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AshTheme.colors.accentContainer)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "🔥 Why This Chapter Matters:",
                        style = AshTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.brand
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chapter.ashRecap.ifBlank { "Master these concepts to build an unbreakable foundation in modern network security." },
                        style = AshTheme.typography.bodyLarge,
                        color = AshTheme.colors.ink,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Learning Goals
            Text(
                text = "LEARNING OBJECTIVES",
                style = AshTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AshTheme.colors.brand
            )
            Spacer(modifier = Modifier.height(8.dp))
            chapter.learningGoals.forEach { goal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🎯", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = goal,
                        style = AshTheme.typography.bodyLarge,
                        color = AshTheme.colors.ink,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Parts of the Chapter
            Text(
                text = "CHAPTER PROGRESSION (4 PARTS)",
                style = AshTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AshTheme.colors.brand
            )
            Spacer(modifier = Modifier.height(8.dp))

            parts.forEachIndexed { index, (partName, partDesc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AshTheme.colors.bg)
                        .border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isCompleted) AshTheme.colors.statusSuccess else AshTheme.colors.border,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = partName,
                            style = AshTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AshTheme.colors.ink
                        )
                        Text(
                            text = partDesc,
                            style = AshTheme.typography.bodySmall,
                            color = AshTheme.colors.subtle,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Shortcut to Visualizer Lab
                AshButton(
                    text = "Open Lab",
                    onClick = onOpenLab,
                    variant = AshButtonStyle.SURFACE,
                    size = AshButtonSize.REGULAR,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = AshTheme.colors.brand, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.weight(0.4f)
                )

                // Primary Start / Continue Button
                AshButton(
                    text = if (isCompleted) "Replay Chapter" else "Start Chapter",
                    onClick = onStartLesson,
                    variant = AshButtonStyle.BRAND,
                    size = AshButtonSize.REGULAR,
                    modifier = Modifier.weight(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
