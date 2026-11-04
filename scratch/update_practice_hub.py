path = "app/src/main/java/com/simats/codeingo/ui/practice/PracticeHubScreen.kt"
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Imports
if "com.simats.codeingo.ui.components.motion.FlipCard" not in content:
    import_target = "import com.simats.codeingo.domain.GameManager"
    import_replacement = """import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.motion.FlipCard
import com.simats.codeingo.ui.components.motion.CardFace"""
    content = content.replace(import_target, import_replacement, 1)

# 2. Replace items(mistakes) in MistakeVaultTab
target_block = """            items(mistakes) { item ->
                val shape = RoundedCornerShape(16.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(CardBackground)
                        .border(
                            1.5.dp,
                            if (item.isRepaired) DuolingoGreen.copy(alpha = 0.5f) else DsaOrange.copy(alpha = 0.5f),
                            shape
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DsaOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.conceptId.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = DsaOrange,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (item.isRepaired) {
                            Text(
                                text = "REPAIRED ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = DuolingoGreen
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DsaOrange)
                                    .clickable { onStartRepair(item) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "REPAIR (+10 XP)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.prompt,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Wrong: ${item.wrongAnswerGiven}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuolingoRed
                        )
                        Text(text = "•", color = SubtextGray)
                        Text(
                            text = "Correct: ${item.correctAnswer}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuolingoGreen
                        )
                    }

                    if (item.conceptTag.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🏷️ ${item.conceptTag}${if (item.misconceptionTag.isNotEmpty()) " • ⚠️ ${item.misconceptionTag}" else ""}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DsaOrange
                        )
                    }
                }
            }"""

replacement_block = """            items(mistakes) { item ->
                FlippableMistakeCard(item = item, onStartRepair = onStartRepair)
            }"""

content = content.replace(target_block, replacement_block, 1)

# 3. Add FlippableMistakeCard composable function
flippable_composable = """
@Composable
private fun FlippableMistakeCard(
    item: MistakeVaultItem,
    onStartRepair: (MistakeVaultItem) -> Unit
) {
    var cardFace by remember(item.id) { mutableStateOf(CardFace.FRONT) }
    val shape = RoundedCornerShape(16.dp)

    FlipCard(
        cardFace = cardFace,
        onFlip = { next -> cardFace = next },
        enableClickFlip = true,
        modifier = Modifier.fillMaxWidth(),
        frontContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(CardBackground)
                    .border(
                        1.5.dp,
                        if (item.isRepaired) DuolingoGreen.copy(alpha = 0.5f) else DsaOrange.copy(alpha = 0.5f),
                        shape
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DsaOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.conceptId.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = DsaOrange,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (item.isRepaired) {
                        Text(
                            text = "REPAIRED ✨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = DuolingoGreen
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DsaOrange)
                                .clickable { onStartRepair(item) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "REPAIR (+10 XP)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = item.prompt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Vulnerability: ${item.wrongAnswerGiven}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuolingoRed
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.conceptTag.isNotEmpty()) {
                        Text(
                            text = "🏷️ ${item.conceptTag}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DsaOrange
                        )
                    }
                    Text(
                        text = "🔄 TAP TO FLIP PATCH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }
        },
        backContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, DuolingoGreen, shape)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ SECURITY PATCH VERIFIED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = DuolingoGreen
                    )
                    Text(
                        text = "🔄 FLIP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }

                Text(
                    text = "Correct Principle:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtextGray
                )
                Text(
                    text = item.correctAnswer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoGreen
                )

                if (item.misconceptionTag.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⚠️ Misconception: ${item.misconceptionTag}",
                            fontSize = 11.sp,
                            color = Color(0xFFF97316)
                        )
                    }
                }
            }
        }
    )
}
"""

if "private fun FlippableMistakeCard" not in content:
    content += flippable_composable

with open(path, "w", encoding="utf-8") as f:
    f.write(content)
print("PracticeHubScreen.kt updated successfully.")

