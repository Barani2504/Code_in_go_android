package com.simats.codeingo.ui.components.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.guide.CryptoGlossary
import com.simats.codeingo.domain.guide.GlossaryTerm
import com.simats.codeingo.ui.components.AshMono
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.AshType

/**
 * Inline or standalone chip with dotted underline.
 * When tapped, opens an Ash 1-sentence plain-English definition dialog with concrete example.
 */
@Composable
fun GlossaryChip(
    term: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    textColor: Color = AshTheme.colors.brand
) {
    var showDialog by remember { mutableStateOf(false) }
    val glossaryTerm = remember(term) { CryptoGlossary.findTerm(term) }

    val dotColor = AshTheme.colors.brand.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = dotColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showDialog = true
            }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Text(
            text = term,
            style = AshType.bodyMedium.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }

    if (showDialog) {
        GlossaryTermDialog(
            term = glossaryTerm ?: GlossaryTerm(
                id = "custom",
                term = term,
                shortDefinition = "A fundamental concept in cryptography and network security.",
                example = "Used throughout modern secure communication protocols.",
                category = "General"
            ),
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun GlossaryTermDialog(
    term: GlossaryTerm,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AshTheme.colors.brand.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📖", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = term.term,
                        style = AshType.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        ),
                        color = AshTheme.colors.ink
                    )
                    Text(
                        text = term.category.uppercase(),
                        style = AshType.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AshTheme.colors.brand
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = term.shortDefinition,
                    style = AshType.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = AshTheme.colors.ink
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AshTheme.colors.surface)
                        .border(1.dp, AshTheme.colors.accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 EXAMPLE",
                            style = AshType.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = AshTheme.colors.accent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = term.example,
                            style = AshType.bodyMedium.copy(
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            ),
                            color = AshTheme.colors.inkMuted
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got It!",
                    style = AshType.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = AshTheme.colors.brand
                    )
                )
            }
        },
        containerColor = AshTheme.colors.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
