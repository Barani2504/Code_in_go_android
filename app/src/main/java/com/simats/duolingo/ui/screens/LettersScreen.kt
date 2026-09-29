package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.*
import com.simats.duolingo.ui.theme.*

// ─── Letters / Code-Along Tab ─────────────────────────────────────────────────
// Mirrors LettersView.swift — shows an interactive code alphabet panel
@Composable
fun LettersScreen() {
    val categories = listOf(
        "Variables" to listOf(
            "int"    to "Integer",
            "str"    to "String",
            "bool"   to "Boolean",
            "float"  to "Float",
            "list"   to "List",
            "dict"   to "Dictionary",
        ),
        "Control Flow" to listOf(
            "if"     to "Conditional",
            "else"   to "Alternative",
            "for"    to "Loop",
            "while"  to "While Loop",
            "break"  to "Exit Loop",
            "return" to "Return Value",
        ),
        "Functions" to listOf(
            "def"    to "Define Function",
            "class"  to "Define Class",
            "import" to "Import Module",
            "from"   to "Import From",
            "lambda" to "Anonymous Fn",
            "yield"  to "Generator",
        ),
    )

    var selectedCategory by remember { mutableStateOf(categories.first().first) }
    var selectedKeyword  by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        // Category tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DuolingoHeaderBg)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories.map { it.first }) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DuolingoGreen else DuolingoCardBg)
                        .border(
                            width = if (isSelected) 0.dp else 1.dp,
                            color = DuolingoInputBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        cat,
                        color = if (isSelected) Color.White else DuolingoSubtext,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Keyword grid
        val keywords = categories.firstOrNull { it.first == selectedCategory }?.second ?: emptyList()
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            keywords.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { (kw, desc) ->
                        val isSelected = selectedKeyword?.first == kw
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) DuolingoGreen.copy(0.15f) else DuolingoCardBg)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) DuolingoGreen else DuolingoInputBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedKeyword = if (isSelected) null else (kw to desc) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = kw,
                                    color = DuolingoGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                )
                                Text(
                                    text = desc,
                                    color = DuolingoSubtext,
                                    fontSize = 10.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            }
                        }
                    }
                    // fill remaining cells if row < 3
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            // Detail panel for selected keyword
            selectedKeyword?.let { (kw, desc) ->
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg)
                        .border(2.dp, DuolingoGreen, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DuolingoGreen.copy(0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(kw, color = DuolingoGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        Text(desc, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Keyword: $kw\nUsed to define $desc in code.\n\nExample:\n  $kw myVariable = 42",
                        color = DuolingoSubtext,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
