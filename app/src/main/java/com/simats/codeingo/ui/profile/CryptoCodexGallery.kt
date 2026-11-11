package com.simats.codeingo.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.CardFace
import com.simats.codeingo.ui.components.motion.FlipCard
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.components.motion.shimmerGlossSweep
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * Historical Cryptographer Model for Codex Cards.
 */
data class CryptographerCard(
    val id: String,
    val name: String,
    val epithet: String,
    val avatar: String,
    val year: String,
    val algorithm: String,
    val rarity: CodexRarity,
    val principle: String,
    val legacy: String,
    val requiredChapter: Int
)

enum class CodexRarity(val label: String, val color: Color, val borderBrush: Brush) {
    RARE("RARE", Color(0xFF38BDF8), Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8)))),
    EPIC("EPIC", Color(0xFF10B981), Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF34D399)))),
    LEGENDARY("LEGENDARY", Color(0xFFA855F7), Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFC084FC)))),
    MYTHIC("MYTHIC", AmberGold, Brush.linearGradient(listOf(Color(0xFFD97706), AmberGold, Color(0xFFFBBF24))))
}

val ALL_CODEX_CARDS = listOf(
    CryptographerCard(
        id = "shannon",
        name = "Claude Shannon",
        epithet = "Father of Information Theory",
        avatar = "📡",
        year = "1949",
        algorithm = "One-Time Pad & Entropy",
        rarity = CodexRarity.MYTHIC,
        principle = "Perfect secrecy requires key entropy H(K) >= message entropy H(M). The ciphertext reveals zero statistical information.",
        legacy = "Founded information theory and mathematically proved unconditional security.",
        requiredChapter = 1
    ),
    CryptographerCard(
        id = "feistel",
        name = "Horst Feistel",
        epithet = "Architect of Symmetric Ciphers",
        avatar = "🧱",
        year = "1973",
        algorithm = "Feistel Cipher Structure",
        rarity = CodexRarity.EPIC,
        principle = "Splits plaintext into left and right halves. Round functions need not be invertible because the structure is inherently reversible.",
        legacy = "Formed the bedrock of DES, Blowfish, and modern block cipher design.",
        requiredChapter = 2
    ),
    CryptographerCard(
        id = "merkle",
        name = "Ralph Merkle",
        epithet = "Pioneer of Hash Trees & Puzzles",
        avatar = "🌲",
        year = "1979",
        algorithm = "Merkle Tree Authentication",
        rarity = CodexRarity.RARE,
        principle = "Hierarchical binary tree of cryptographic hashes. Verifies massive data integrity in O(log N) proofs.",
        legacy = "Foundational to Git commits, Certificate Transparency, and blockchain verification.",
        requiredChapter = 3
    ),
    CryptographerCard(
        id = "diffie_hellman",
        name = "Diffie & Hellman",
        epithet = "Inventors of Key Exchange",
        avatar = "🔑",
        year = "1976",
        algorithm = "Diffie-Hellman Protocol",
        rarity = CodexRarity.LEGENDARY,
        principle = "Relies on the Discrete Logarithm Problem in cyclic groups. Two parties agree on a shared secret over an insecure channel.",
        legacy = "Created the entire field of public-key cryptography.",
        requiredChapter = 4
    ),
    CryptographerCard(
        id = "rsa",
        name = "Rivest, Shamir & Adleman",
        epithet = "Architects of Public-Key RSA",
        avatar = "🏛️",
        year = "1977",
        algorithm = "RSA Trapdoor Permutation",
        rarity = CodexRarity.LEGENDARY,
        principle = "Relies on the hardness of factoring the product of two massive primes: n = p * q with Euler's totient phi(n).",
        legacy = "Secured internet web browsing, SSL/TLS, and modern software code signing.",
        requiredChapter = 5
    ),
    CryptographerCard(
        id = "daemen_rijmen",
        name = "Daemen & Rijmen",
        epithet = "Designers of AES / Rijndael",
        avatar = "🛡️",
        year = "1998",
        algorithm = "Advanced Encryption Standard",
        rarity = CodexRarity.MYTHIC,
        principle = "Substitution-Permutation Network operating on a 4x4 state matrix in Galois Field GF(2^8). Resists linear and differential cryptanalysis.",
        legacy = "Adopted worldwide by NIST as the global standard for symmetric data encryption.",
        requiredChapter = 2
    )
)

/**
 * 🏆 Crypto Codex Collectible Cards Gallery
 * Interactive 3D flippable knowledge cards celebrating legendary cryptographers.
 */
@Composable
fun CryptoCodexGallery(
    unlockedChapter: Int = 5,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📜", fontSize = 18.sp)
                Text(
                    text = "CRYPTO CODEX",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }

            val unlockedCount = ALL_CODEX_CARDS.count { it.requiredChapter <= unlockedChapter }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberGold.copy(alpha = 0.15f))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$unlockedCount / ${ALL_CODEX_CARDS.size} COLLECTED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ALL_CODEX_CARDS.forEach { card ->
                val isUnlocked = card.requiredChapter <= unlockedChapter
                CodexItemCard(card = card, isUnlocked = isUnlocked)
            }
        }
    }
}

@Composable
private fun CodexItemCard(
    card: CryptographerCard,
    isUnlocked: Boolean
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    var cardFace by remember { mutableStateOf(CardFace.FRONT) }

    val cardBg = if (isDark) {
        if (isUnlocked) Color(0xFF111827) else Color(0xFF0B0F19)
    } else {
        if (isUnlocked) Color.White else Color(0xFFF8FAFC)
    }
    val cardBorder = if (isUnlocked) {
        card.rarity.borderBrush
    } else {
        Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        )
    }

    FlipCard(
        cardFace = cardFace,
        onFlip = { next -> if (isUnlocked) cardFace = next },
        enableClickFlip = isUnlocked,
        modifier = Modifier.width(220.dp),
        frontContent = {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(
                        width = if (isUnlocked) 2.dp else 1.dp,
                        brush = cardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isUnlocked) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = if (isDark) SubtextGray else dynamicColors.textSecondary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "LOCKED CODEX",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) SubtextGray else dynamicColors.textSecondary
                        )
                        Text(
                            text = "Master Chapter ${card.requiredChapter} to decipher this legendary cryptographer.",
                            fontSize = 11.sp,
                            color = if (isDark) SubtextGray.copy(alpha = 0.8f) else dynamicColors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(card.rarity.color.copy(alpha = 0.18f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = card.rarity.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = card.rarity.color
                                )
                            }
                            Text(
                                text = card.year,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SubtextGray else dynamicColors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = card.avatar, fontSize = 48.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = card.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = card.epithet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = dynamicColors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.06f) else card.rarity.color.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡ ${card.algorithm}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = card.rarity.color,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "🔄 TAP TO INSPECT PROOF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }
                }
            }
        },
        backContent = {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0xFF0F172A) else Color.White)
                    .border(1.8.dp, card.rarity.color, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY PRINCIPLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = card.rarity.color
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = card.rarity.color,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = card.principle,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = dynamicColors.textPrimary,
                        lineHeight = 16.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Legacy: ${card.legacy}",
                            fontSize = 10.sp,
                            color = dynamicColors.textSecondary,
                            lineHeight = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔄 TAP TO FLIP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }
                }
            }
        }
    )
}

