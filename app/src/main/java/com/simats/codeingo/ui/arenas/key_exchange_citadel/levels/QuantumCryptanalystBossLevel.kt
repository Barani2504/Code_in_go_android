package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun QuantumCryptanalystBossLevel(
    onDefeatBoss: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Quantum Cryptanalyst prepares Shor's algorithm! Deploy quantum-safe defenses!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("Shor's Factorization: Which widely deployed cipher is broken in polynomial time by Shor's algorithm?", "RSA-2048", listOf("RSA-2048", "AES-256", "SHA-256", "ChaCha20"))
        2 -> Triple("Shor's Discrete Log: Which key agreement protocol is broken by quantum computers?", "ECDH (Curve25519)", listOf("ECDH (Curve25519)", "AES-GCM", "HMAC-SHA256", "Argon2id"))
        else -> Triple("Post-Quantum Cryptography: Which NIST standardized lattice-based KEM provides Shor resistance?", "ML-KEM (Kyber)", listOf("ML-KEM (Kyber)", "RSA-4096", "Diffie-Hellman", "ECC-P384"))
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
            Text(text = "👾 THE QUANTUM CRYPTANALYST", fontSize = 15.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

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
                    .background(DuolingoRed)
            )
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)

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
                Text(text = phaseData.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            phaseData.third.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { opt ->
                        val isCorrect = opt == phaseData.second
                        val isSelected = selectedAnswer == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed)
                                    else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                )
                                .border(1.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedAnswer = opt
                                    if (isCorrect) {
                                        onActionSuccess()
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase >= 3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase++
                                            selectedAnswer = null
                                            feedback = "💥 Quantum qubits decohered! Next quantum attack phase!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Quantum attack breached your defenses! Classical asymmetric ciphers are vulnerable to Shor's algorithm."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else dynamicColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
