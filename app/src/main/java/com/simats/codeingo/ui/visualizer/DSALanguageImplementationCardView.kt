package com.simats.codeingo.ui.visualizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun DSALanguageImplementationCardView(
    structureName: String,
    modifier: Modifier = Modifier,
    initialLanguage: String = "Python"
) {
    val context = LocalContext.current
    var activeLanguage by remember(initialLanguage) { mutableStateOf(initialLanguage) }
    var isExpanded by remember { mutableStateOf(true) }
    var copied by remember { mutableStateOf(false) }

    val supportedLanguages = listOf("Python", "Java", "C++", "Go", "JavaScript", "Kotlin", "Swift")

    val languageFlag = when (activeLanguage.lowercase()) {
        "python" -> "🐍"
        "java" -> "☕"
        "c++", "cpp" -> "⚙️"
        "go" -> "🐹"
        "javascript", "js" -> "📜"
        "kotlin" -> "📱"
        else -> "🦅"
    }

    val codeString = DSALanguageCodeProvider.code(structureName, activeLanguage)
    val lines = codeString.lines()

    val complexityChips = when (structureName.lowercase()) {
        "stack" -> listOf("Push" to "O(1)", "Pop" to "O(1)", "Peek" to "O(1)", "Space" to "O(n)")
        "queue" -> listOf("Enqueue" to "O(1)", "Dequeue" to "O(1)", "Front" to "O(1)", "Space" to "O(n)")
        "linked list", "linkedlist" -> listOf("Insert Head" to "O(1)", "Search" to "O(n)", "Delete" to "O(n)", "Space" to "O(n)")
        "binary tree", "binarytree", "tree", "bst" -> listOf("Search" to "O(h)", "Insert" to "O(h)", "Inorder" to "O(n)", "Space" to "O(h)")
        "avl tree", "avl" -> listOf("Search" to "O(log n)", "Insert" to "O(log n)", "Rotate" to "O(1)", "Space" to "O(n)")
        "trie" -> listOf("Insert" to "O(L)", "Search" to "O(L)", "Prefix" to "O(L)", "Space" to "O(N*L)")
        "graph" -> listOf("BFS" to "O(V+E)", "DFS" to "O(V+E)", "Add Edge" to "O(1)", "Space" to "O(V+E)")
        else -> listOf("Access" to "O(1)", "Search" to "O(n)", "Append" to "O(1)", "Space" to "O(n)")
    }

    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CardBackground)
            .border(1.5.dp, InputBorder, cardShape)
    ) {
        // Card Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressScale()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DsaBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = languageFlag, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$structureName Implementation",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Text(
                    text = "$activeLanguage Code & Complexity Analysis",
                    fontSize = 12.sp,
                    color = SubtextGray
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = SubtextGray
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                // Language Selector Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(supportedLanguages) { lang ->
                        val isSelected = activeLanguage.equals(lang, ignoreCase = true)
                        val chipShape = RoundedCornerShape(12.dp)
                        Box(
                            modifier = Modifier
                                .clip(chipShape)
                                .background(if (isSelected) DsaBlue else DarkBackground)
                                .border(1.dp, if (isSelected) DsaBlue else InputBorder, chipShape)
                                .pressScale()
                                .clickable {
                                    activeLanguage = lang
                                    copied = false
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = lang,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else SubtextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Complexity Chips Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(complexityChips) { (op, time) ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBackground)
                                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$op: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SubtextGray
                            )
                            Text(
                                text = time,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Code Box with Line Numbers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D161C))
                        .border(1.dp, InputBorder.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        // Top bar inside code box: language label + copy button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$activeLanguage • Idiomatic",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SubtextGray
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (copied) DuolingoGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, if (copied) DuolingoGreen else Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .pressScale()
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("$activeLanguage Code", codeString)
                                        clipboard.setPrimaryClip(clip)
                                        copied = true
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy Code",
                                    tint = if (copied) DuolingoGreen else Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copied) "COPIED" else "COPY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (copied) DuolingoGreen else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Scrollable code lines
                        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            Row {
                                // Line Numbers
                                Column(modifier = Modifier.padding(end = 12.dp)) {
                                    lines.indices.forEach { idx ->
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = SubtextGray.copy(alpha = 0.5f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                // Code content
                                Column {
                                    lines.forEach { line ->
                                        val color = when {
                                            line.trimStart().startsWith("#") || line.trimStart().startsWith("//") -> Color(0xFF68D391)
                                            line.contains("class ") || line.contains("def ") || line.contains("func ") -> Color(0xFF63B3ED)
                                            line.contains("return ") || line.contains("raise ") -> Color(0xFFF6AD55)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                        Text(
                                            text = line,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = color,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
