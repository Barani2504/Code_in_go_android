package com.simats.duolingo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.defaultLanguages
import com.simats.duolingo.ui.theme.*

@Composable
fun LanguagePickerSheet(
    onDismiss: () -> Unit,
    onLanguageSelected: () -> Unit = {},
) {
    val selected = AppState.selectedLanguage

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DuolingoDarkBg)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(50))
                    .background(DuolingoSubtext.copy(alpha = 0.4f))
            )

            // Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What do you want to learn?",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Text(
                    text = "Choose your programming language",
                    color = DuolingoSubtext,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }

            // Language list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(defaultLanguages, key = { it.code }) { lang ->
                    val isSelected = selected?.code == lang.code
                    val isDsa = lang.code == "dsa"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected && isDsa -> Color(0xFF2A1F45)
                                    isSelected -> DuolingoCardBg
                                    isDsa -> Color(0xFF1E1938)
                                    else -> DuolingoInputBg
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else if (isDsa) 1.5.dp else 1.dp,
                                color = when {
                                    isSelected && isDsa -> Color(0xFFFF9600)
                                    isSelected -> DuolingoGreen
                                    isDsa -> Color(0xFFFF9600).copy(alpha = 0.5f)
                                    else -> DuolingoInputBorder
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                AppState.selectedLanguage = lang
                                onLanguageSelected()
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(lang.flagEmoji, fontSize = 30.sp)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    lang.nativeName,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isDsa) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFF9600).copy(alpha = 0.2f))
                                            .border(1.dp, Color(0xFFFF9600), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "11 WORLDS",
                                            color = Color(0xFFFF9600),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                } else if (lang.name != lang.nativeName) {
                                    Text(
                                        "(${lang.name})",
                                        color = DuolingoSubtext,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isDsa) "Algorithms & Visual Structures • ${lang.learnersCount}" else lang.learnersCount,
                                color = if (isDsa) Color(0xFFFFC800) else DuolingoSubtext.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = if (isDsa) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        if (isSelected) {
                            Text("✅", fontSize = 22.sp)
                        }
                    }
                }
            }
        }
    }
}
