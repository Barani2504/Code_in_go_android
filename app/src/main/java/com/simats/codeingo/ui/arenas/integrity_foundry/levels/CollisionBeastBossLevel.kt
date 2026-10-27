package com.simats.codeingo.ui.arenas.integrity_foundry.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

enum class CollisionPhase(val phaseNumber: Int) {
    PHASE_1(1),
    PHASE_2(2),
    PHASE_3(3)
}

data class CollisionChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class CollisionChallenge(
    val phase: CollisionPhase,
    val question: String,
    val choices: List<CollisionChoice>
)

@Composable
fun CollisionBeastBossLevel(
    onDefeatBoss: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(CollisionPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Collision Beast attacks cryptographic digest integrity! Fortify hash defenses!") }

    val challenges = remember {
        listOf(
            CollisionChallenge(
                phase = CollisionPhase.PHASE_1,
                question = "Merkle Tree Tamper: If leaf node A is altered in a Merkle tree, which cryptographic element immediately changes?",
                choices = listOf(
                    CollisionChoice("c1_correct", "Parent hash H(A ∥ B) and the Root Hash", true, "Correct: Any change to leaf A cascades upward through parent digests to invalidate the root!"),
                    CollisionChoice("c1_d1", "Only leaf A changes; root remains valid", false, "Misconception: In a Merkle tree, parents are hashes of their children; root integrity breaks immediately."),
                    CollisionChoice("c1_d2", "Sibling node B changes", false, "Misconception: Sibling node B is independent; only the combined parent H(A ∥ B) changes."),
                    CollisionChoice("c1_d3", "Only the private key changes", false, "Misconception: Merkle trees are hash structures, not private key pairs.")
                )
            ),
            CollisionChallenge(
                phase = CollisionPhase.PHASE_2,
                question = "Birthday Bound: Finding a collision in a 128-bit hash function requires approximately how many hash evaluations?",
                choices = listOf(
                    CollisionChoice("c2_correct", "2^64 hash evaluations (~2^(n/2))", true, "Correct: By the Birthday Paradox, square-root search requires ~2^(128/2) = 2^64 trials!"),
                    CollisionChoice("c2_d1", "2^128 hash evaluations", false, "Misconception: 2^128 is the pre-image resistance bound, not the collision bound."),
                    CollisionChoice("c2_d2", "2^32 hash evaluations", false, "Misconception: 2^32 applies to 64-bit hashes, not 128-bit."),
                    CollisionChoice("c2_d3", "2^256 hash evaluations", false, "Misconception: 2^256 is impossible for a 128-bit digest space.")
                )
            ),
            CollisionChallenge(
                phase = CollisionPhase.PHASE_3,
                question = "Length Extension: Which hash design is vulnerable to length extension attacks if keyed as plain H(Key ∥ Msg)?",
                choices = listOf(
                    CollisionChoice("c3_correct", "Merkle-Damgård (e.g. SHA-256, SHA-512)", true, "Correct: Merkle-Damgård exposes its internal state in the output digest, allowing state continuation!"),
                    CollisionChoice("c3_d1", "Keccak Sponge (SHA-3)", false, "Misconception: SHA-3's sponge construction squeezes truncated capacity, immune to extension."),
                    CollisionChoice("c3_d2", "BLAKE3 Tree Hash", false, "Misconception: BLAKE3 uses keyed tree mode and finalize flags that prevent length extension."),
                    CollisionChoice("c3_d3", "HMAC-SHA256", false, "Misconception: HMAC was specifically invented to prevent length extension attacks!")
                )
            )
        )
    }

    val currentChallenge = challenges.first { it.phase == currentPhase }
    val shuffledChoices = remember(currentPhase) {
        currentChallenge.choices.shuffled()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "👾 THE COLLISION BEAST", fontSize = 15.sp, color = ArenaDangerRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE ${currentPhase.phaseNumber} / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        // Boss Health Bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(bossHP / 100f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(ArenaDangerRed)
            )
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)

        // Challenge Riddle Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = currentChallenge.question, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        // Choices Grid (48dp+ touch target)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) (if (choice.isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                )
                                .border(1.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedChoiceId = choice.id
                                    if (choice.isCorrect) {
                                        onActionSuccess()
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase == CollisionPhase.PHASE_3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase = when (currentPhase) {
                                                CollisionPhase.PHASE_1 -> CollisionPhase.PHASE_2
                                                CollisionPhase.PHASE_2 -> CollisionPhase.PHASE_3
                                                CollisionPhase.PHASE_3 -> CollisionPhase.PHASE_3
                                            }
                                            selectedChoiceId = null
                                            feedback = "💥 Collision deflected! ${choice.explanation}"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Counterattack: ${choice.explanation}"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected && choice.isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
