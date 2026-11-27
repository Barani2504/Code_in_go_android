package com.simats.codeingo.ui.visualizer.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AshMono
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.accentContainer
import com.simats.codeingo.ui.theme.background
import com.simats.codeingo.ui.theme.border
import com.simats.codeingo.ui.theme.statusError
import com.simats.codeingo.ui.theme.statusSuccess
import com.simats.codeingo.ui.theme.subtle
import com.simats.codeingo.ui.theme.surfaceContainer
import com.simats.codeingo.ui.visualizer.engine.SafeCryptoEngine

/**
 * 🔬 UniversalVisualizerView — Powers all 25 visualizers with interactive Try/Watch beats.
 *
 * Implements:
 * 1. Caesar Shift Slider & Playfair Wheel
 * 2. Diffie-Hellman Color & Key Mixing
 * 3. SHA-256 Avalanche Live Meter
 * 4. RSA Tiny Prime Math Stepper
 * 5. Universal Interactive Sandbox for any syllabus lab
 */
@Composable
fun UniversalVisualizerView(
    spec: VisualizerSpec,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentBeat by remember(spec.id) { mutableStateOf(VisualizerBeat.STORY) }
    var watchStepIndex by remember(spec.id) { mutableIntStateOf(0) }
    var showDetails by remember(spec.id) { mutableStateOf(false) }

    // Dynamic State for interactive TRY beat
    var textInput by remember(spec.id) { mutableStateOf("HELLO") }
    var shiftValue by remember(spec.id) { mutableIntStateOf(3) }
    var modA by remember(spec.id) { mutableStateOf("17") }
    var modM by remember(spec.id) { mutableStateOf("5") }
    val selectedColorAlice = Color(0xFFEF4444) // Red
    val selectedColorBob = Color(0xFF3B82F6)   // Blue

    // Computed what-changed description
    val whatChanged = when (spec.id) {
        "cia_triad" -> "Classified security controls into Confidentiality, Integrity, and Availability."
        "attack_defense_sorter" -> "Sorted active traffic modification vs passive eavesdropping threats."
        "substitution_lab" -> "Shifted by $shiftValue: 'H' becomes '${SafeCryptoEngine.safeCaesar("H", shiftValue).getOrNull()?.ciphertext}'"
        "transposition_stego" -> "Reordered letters into zig-zag rails and inspected LSB pixel bit."
        "sha256_avalanche" -> "Input '${textInput}': 256-bit hash recomputed in real time."
        "modular_clock" -> {
            val a = modA.toLongOrNull() ?: 17L
            val m = modM.toLongOrNull() ?: 5L
            val res = SafeCryptoEngine.safeModulo(a, m).getOrNull()
            "$a mod $m = ${res?.remainder ?: 2}"
        }
        "prime_sieve" -> "Filtered composite multiples to isolate primes and test p × q = N."
        "crt_discrete_log" -> "Computed modular exponentiation g^x mod p around the prime clock."
        "dh_elgamal_lab" -> "Alice and Bob mixed secret exponents to reach an identical shared key."
        "aes_state_matrix" -> "128-bit state matrix transformed across SubBytes & ShiftRows."
        "des_explorer", "feistel_builder" -> "Feistel L/R halves swapped with F-function round output."
        "block_modes_rc4" -> "ECB vs CBC vs CTR encryption modes compared."
        "ecc_point_addition" -> "Secp256k1 curve secant tangent bounced to P + Q point."
        "hmac_lab" -> "HMAC-SHA256 seal verified against message tampering."
        "sign_verify_lab" -> "Signed digest with private key and tested tamper detection."
        "ipsec_pgp" -> "Compared IPsec Transport vs Tunnel packet encapsulation."
        "tls_wifi_handshake" -> "TLS 1.3 ClientHello / ServerHello 1-RTT key derivation completed."
        "firewall_builder" -> "Evaluated packet headers against ordered ALLOW/DENY firewall rules."
        "sqli_dos_sandbox" -> "Tested SQL injection payload against string-concat vs prepared queries."
        else -> null
    }

    VisualizerScaffold(
        spec = spec,
        currentBeat = currentBeat,
        onBeatChange = { currentBeat = it },
        onBackClick = onBackClick,
        watchStepIndex = watchStepIndex,
        totalWatchSteps = spec.watchSteps.size,
        onWatchStepChange = { watchStepIndex = it },
        whatChangedText = whatChanged,
        showDetails = showDetails,
        onToggleDetails = { showDetails = it },
        modifier = modifier,
        watchContent = { stepIdx, details ->
            WatchBeatContent(spec = spec, stepIndex = stepIdx, showDetails = details)
        },
        tryContent = { details ->
            when (spec.id) {
                "cia_triad" -> CiaTriadTryContent(showDetails = details)
                "attack_defense_sorter" -> AttackDefenseTryContent(showDetails = details)
                "substitution_lab" -> CaesarTryContent(
                    text = textInput,
                    onTextChange = { textInput = it },
                    shift = shiftValue,
                    onShiftChange = { shiftValue = it },
                    showDetails = details
                )
                "transposition_stego" -> TranspositionStegoTryContent(showDetails = details)
                "cryptanalysis_lab" -> com.simats.codeingo.ui.visualizer.VigenereCipherVisualizer()
                "sha256_avalanche" -> Sha256TryContent(
                    text = textInput,
                    onTextChange = { textInput = it },
                    showDetails = details
                )
                "dh_elgamal_lab" -> DhTryContent(showDetails = details)
                "modular_clock" -> ModuloTryContent(
                    aStr = modA,
                    onAChange = { modA = it },
                    mStr = modM,
                    onMChange = { modM = it },
                    showDetails = details
                )
                "prime_sieve" -> PrimeSieveTryContent(showDetails = details)
                "crt_discrete_log" -> DiscreteLogTryContent(showDetails = details)
                "rsa_stepper" -> com.simats.codeingo.ui.visualizer.RSAPublicKeyVisualizer()
                "feistel_builder", "des_explorer" -> com.simats.codeingo.ui.visualizer.DESFeistelVisualizer()
                "aes_state_matrix" -> com.simats.codeingo.ui.visualizer.AESMatrixVisualizer()
                "block_modes_rc4" -> com.simats.codeingo.ui.visualizer.EcbCbcCtrVisualizer()
                "ecc_point_addition" -> com.simats.codeingo.ui.visualizer.ECCPointVisualizer()
                "hmac_lab" -> com.simats.codeingo.ui.visualizer.HMACCollisionVisualizer()
                "sign_verify_lab" -> SignVerifyTryContent(showDetails = details)
                "auth_challenge" -> com.simats.codeingo.ui.visualizer.PasswordHashingVisualizer()
                "kerberos_pki" -> com.simats.codeingo.ui.visualizer.KerberosVisualizer()
                "ipsec_pgp" -> IpsecTryContent(showDetails = details)
                "tls_wifi_handshake" -> com.simats.codeingo.ui.visualizer.TLS13HandshakeVisualizer()
                "firewall_builder" -> FirewallTryContent(showDetails = details)
                "sqli_dos_sandbox" -> SqliTryContent(showDetails = details)
                "zkp_merkle_pqc" -> com.simats.codeingo.ui.visualizer.MerkleTreeVisualizer()
                else -> CiaTriadTryContent(showDetails = details)
            }
        },
        onCheckCompleted = {
            onBackClick()
        }
    )
}

// ── WATCH BEAT COMPONENT ─────────────────────────────────────────

@Composable
private fun WatchBeatContent(spec: VisualizerSpec, stepIndex: Int, showDetails: Boolean) {
    val step = spec.watchSteps.getOrNull(stepIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Step Timeline Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            spec.watchSteps.forEachIndexed { idx, _ ->
                val isCurrent = idx == stepIndex
                val isDone = idx < stepIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isCurrent -> AshTheme.colors.brand
                                isDone -> AshTheme.colors.brand.copy(alpha = 0.25f)
                                else -> AshTheme.colors.background
                            }
                        )
                        .border(
                            1.dp,
                            if (isCurrent || isDone) AshTheme.colors.brand else AshTheme.colors.border,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDone) "✓ ${idx + 1}" else "Step ${idx + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCurrent -> Color.White
                            isDone -> AshTheme.colors.brand
                            else -> AshTheme.colors.subtle
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "${spec.story.iconEmoji}  ${spec.story.conceptName.uppercase()}",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = step.narration,
            style = AshTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.ink,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 23.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Visual State Card showing Input -> Transformation -> Result
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AshTheme.colors.background)
                .border(2.dp, AshTheme.colors.brand, RoundedCornerShape(12.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⚡ STEP ${stepIndex + 1} STATE CHANGE",
                    style = AshTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AshTheme.colors.brand
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = step.whatChanged,
                    style = AshTheme.typography.bodyLarge,
                    color = AshTheme.colors.ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                if (step.detailNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AshTheme.colors.accentContainer)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        AshMono(
                            text = step.detailNotes,
                            color = AshTheme.colors.brand,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showDetails) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AshTheme.colors.surfaceContainer)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "🔬 Beginner Tip & Context:",
                        style = AshTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.subtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = spec.lostHelp.takeawayBullet,
                        style = AshTheme.typography.bodyMedium,
                        color = AshTheme.colors.ink,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ── TRY BEAT: CAESAR SHIFT ───────────────────────────────────────

@Composable
private fun CaesarTryContent(
    text: String,
    onTextChange: (String) -> Unit,
    shift: Int,
    onShiftChange: (Int) -> Unit,
    showDetails: Boolean
) {
    val result = SafeCryptoEngine.safeCaesar(text, shift).getOrDefault(
        SafeCryptoEngine.CaesarResult(text, shift, text, "")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: ADJUST THE SHIFT DIAL",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Input Field
        Text(text = "Plaintext Message:", style = AshTheme.typography.labelMedium, color = AshTheme.colors.subtle)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.background)
                .border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = TextStyle(
                    color = AshTheme.colors.ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(AshTheme.colors.brand),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Shift Key: K = $shift",
            style = AshTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.ink
        )
        Slider(
            value = shift.toFloat(),
            onValueChange = { onShiftChange(it.toInt()) },
            valueRange = 0f..25f,
            steps = 24,
            colors = SliderDefaults.colors(
                thumbColor = AshTheme.colors.brand,
                activeTrackColor = AshTheme.colors.brand,
                inactiveTrackColor = AshTheme.colors.border
            )
        )

        Spacer(modifier = Modifier.height(14.dp))
        // Output Cipher Card
        Text(text = "Encrypted Ciphertext:", style = AshTheme.typography.labelMedium, color = AshTheme.colors.subtle)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.accentContainer)
                .padding(14.dp)
        ) {
            AshMono(
                text = result.ciphertext,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = AshTheme.colors.brand
            )
        }

        if (showDetails) {
            Spacer(modifier = Modifier.height(12.dp))
            AshMono(
                text = "Formula: C[i] = (P[i] + $shift) mod 26\nDecryption: P[i] = (C[i] - $shift + 26) mod 26",
                fontSize = 12.sp,
                color = AshTheme.colors.subtle
            )
        }
    }
}

// ── TRY BEAT: SHA-256 AVALANCHE ──────────────────────────────────

@Composable
private fun Sha256TryContent(text: String, onTextChange: (String) -> Unit, showDetails: Boolean) {
    val hash = SafeCryptoEngine.safeSha256(text).getOrDefault("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: TYPE ANY LETTER TO TRIGGER AVALANCHE",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Input String:", style = AshTheme.typography.labelMedium, color = AshTheme.colors.subtle)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.background)
                .border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                cursorBrush = SolidColor(AshTheme.colors.brand),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "256-Bit SHA-256 Digest (64 hex):", style = AshTheme.typography.labelMedium, color = AshTheme.colors.subtle)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.accentContainer)
                .padding(14.dp)
        ) {
            AshMono(
                text = hash,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AshTheme.colors.brand
            )
        }

        if (showDetails) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Length: 256 bits (32 bytes) | Preimage Resistance: 2^256 | Collision Resistance: 2^128",
                style = AshTheme.typography.labelSmall,
                color = AshTheme.colors.subtle,
                fontSize = 12.sp
            )
        }
    }
}

// ── TRY BEAT: DIFFIE-HELLMAN INTERACTIVE ─────────────────────────

private fun modPow(base: Long, exp: Long, mod: Long): Long {
    var result = 1L
    var b = base % mod
    var e = exp
    while (e > 0) {
        if (e % 2L == 1L) result = (result * b) % mod
        b = (b * b) % mod
        e /= 2L
    }
    return result
}

@Composable
private fun DhTryContent(showDetails: Boolean) {
    val p = 23L
    val g = 5L
    var aliceSecret by remember { mutableIntStateOf(6) }
    var bobSecret by remember { mutableIntStateOf(15) }

    val alicePub = modPow(g, aliceSecret.toLong(), p)
    val bobPub = modPow(g, bobSecret.toLong(), p)
    val sharedAlice = modPow(bobPub, aliceSecret.toLong(), p)
    val sharedBob = modPow(alicePub, bobSecret.toLong(), p)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: ADJUST SECRET EXPONENTS (p=23, g=5)",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Alice's Secret (a = $aliceSecret):", style = AshTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
        Slider(
            value = aliceSecret.toFloat(),
            onValueChange = { aliceSecret = it.toInt() },
            valueRange = 2f..20f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFFEF4444), activeTrackColor = Color(0xFFEF4444))
        )

        Text(text = "Bob's Secret (b = $bobSecret):", style = AshTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
        Slider(
            value = bobSecret.toFloat(),
            onValueChange = { bobSecret = it.toInt() },
            valueRange = 2f..20f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF3B82F6), activeTrackColor = Color(0xFF3B82F6))
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Alice Sends Public A", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                    AshMono(text = "5^$aliceSecret mod 23 = $alicePub", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF3B82F6).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Bob Sends Public B", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                    AshMono(text = "5^$bobSecret mod 23 = $bobPub", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF3B82F6))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.statusSuccess.copy(alpha = 0.15f))
                .border(1.5.dp, AshTheme.colors.statusSuccess, RoundedCornerShape(10.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎉 Identical Shared Secret Key: $sharedAlice",
                    color = AshTheme.colors.statusSuccess,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                if (showDetails) {
                    Spacer(modifier = Modifier.height(4.dp))
                    AshMono(
                        text = "Alice: $bobPub^$aliceSecret mod 23 = $sharedAlice | Bob: $alicePub^$bobSecret mod 23 = $sharedBob",
                        fontSize = 11.sp,
                        color = AshTheme.colors.ink
                    )
                }
            }
        }
    }
}

// ── TRY BEAT: MODULO & GCD ───────────────────────────────────────

@Composable
private fun ModuloTryContent(aStr: String, onAChange: (String) -> Unit, mStr: String, onMChange: (String) -> Unit, showDetails: Boolean) {
    val a = aStr.toLongOrNull() ?: 17L
    val m = mStr.toLongOrNull() ?: 5L
    val result = SafeCryptoEngine.safeModulo(a, m).getOrDefault(SafeCryptoEngine.ModuloResult(a, m, 2, 1))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: CALCULATE MODULO & GCD",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Number (A):", style = AshTheme.typography.labelSmall, color = AshTheme.colors.subtle)
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(8.dp)).padding(10.dp)) {
                    BasicTextField(value = aStr, onValueChange = onAChange, textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Modulus (M):", style = AshTheme.typography.labelSmall, color = AshTheme.colors.subtle)
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(8.dp)).padding(10.dp)) {
                    BasicTextField(value = mStr, onValueChange = onMChange, textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.accentContainer)
                .padding(14.dp)
        ) {
            Column {
                AshMono(text = "$a mod $m = ${result.remainder}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
                Spacer(modifier = Modifier.height(4.dp))
                AshMono(text = "gcd($a, $m) = ${result.gcd}", fontSize = 14.sp, color = AshTheme.colors.ink)
            }
        }
    }
}

// ── TRY BEAT: RSA TINY PRIMES ────────────────────────────────────

@Composable
private fun RsaTryContent(showDetails: Boolean) {
    val rsa = SafeCryptoEngine.safeRsaTiny(pInput = 3, qInput = 11, eInput = 3, messageInput = 7).getOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: RSA ENCRYPTION WITH TINY PRIMES",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (rsa != null) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AshMono(text = "Primes: p = ${rsa.p}, q = ${rsa.q}", fontSize = 14.sp, color = AshTheme.colors.ink)
                AshMono(text = "Modulus: n = p * q = ${rsa.n}", fontSize = 14.sp, color = AshTheme.colors.ink)
                AshMono(text = "Totient: phi(n) = ${rsa.phi}", fontSize = 14.sp, color = AshTheme.colors.ink)
                AshMono(text = "Public Key: (e = ${rsa.e}, n = ${rsa.n})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
                AshMono(text = "Private Key: d = ${rsa.d}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.accentContainer).padding(10.dp)
                ) {
                    AshMono(
                        text = "Message M = ${rsa.message} -> Encrypt C = ${rsa.ciphertext} -> Decrypt M' = ${rsa.decrypted}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.brand
                    )
                }
            }
        }
    }
}

// ── 1. CIA TRIAD TAP-GAME ────────────────────────────────────────

@Composable
private fun CiaTriadTryContent(showDetails: Boolean) {
    val scenarios = remember {
        listOf(
            Triple("Encrypting medical records with AES-256 so only doctors can read them", "Confidentiality", "Encryption hides data from unauthorized eyes."),
            Triple("Checking a SHA-256 checksum before installing an app update", "Integrity", "Hashes detect if a single bit was tampered with."),
            Triple("Adding DDoS protection & backup servers so your site stays online 24/7", "Availability", "Redundancy keeps services reachable for real users."),
            Triple("An attacker alters a bank wire amount from \$100 to \$9,000 in transit", "Integrity", "Tampering with data violates Integrity.")
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var picked by remember(index) { mutableStateOf<String?>(null) }
    val current = scenarios[index]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text("TRY: CLASSIFY THE SCENARIO (${index + 1}/${scenarios.size})", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(12.dp)).padding(14.dp)
        ) {
            Text(current.first, style = AshTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
        }
        Spacer(Modifier.height(12.dp))
        listOf("Confidentiality", "Integrity", "Availability").forEach { pillar ->
            val isPicked = picked == pillar
            val isCorrect = pillar == current.second
            val bg = when {
                picked == null -> AshTheme.colors.background
                isCorrect -> AshTheme.colors.statusSuccess.copy(alpha = 0.18f)
                isPicked -> AshTheme.colors.statusError.copy(alpha = 0.18f)
                else -> AshTheme.colors.background
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bg)
                    .border(1.2.dp, if (isPicked) AshTheme.colors.brand else AshTheme.colors.border, RoundedCornerShape(10.dp))
                    .clickable { picked = pillar }
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Text(pillar, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
            }
        }
        if (picked != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (picked == current.second) "✅ Correct! ${current.third}" else "❌ That's ${current.second}. ${current.third}",
                color = if (picked == current.second) AshTheme.colors.statusSuccess else AshTheme.colors.statusError,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { index = (index + 1) % scenarios.size },
                colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.brand)
            ) {
                Text("Next Scenario ➔", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (showDetails) {
            Spacer(Modifier.height(8.dp))
            AshMono("NIST Triad: C = Secrecy | I = Untampered | A = Uptime", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 2. ATTACK OR DEFENSE? SORTER ─────────────────────────────────

@Composable
private fun AttackDefenseTryContent(showDetails: Boolean) {
    val items = remember {
        listOf(
            Triple("Sniffing unencrypted Wi-Fi packets at a coffee shop", "Passive", "Reads traffic silently without altering any bits."),
            Triple("Injecting fake ARP replies to hijack traffic (MITM)", "Active", "Actively alters network state and packet flow."),
            Triple("Flooding a web server with millions of SYN requests", "Active", "Disrupts availability by exhausting server queues."),
            Triple("Analyzing packet timing & sizes to infer who talks to whom", "Passive", "Traffic analysis observes metadata without modifying packets.")
        )
    }
    var idx by remember { mutableIntStateOf(0) }
    var choice by remember(idx) { mutableStateOf<String?>(null) }
    val item = items[idx]

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: PASSIVE (LISTEN) OR ACTIVE (MODIFY)?", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(10.dp))
        Text(item.first, style = AshTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Passive", "Active").forEach { kind ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                choice == null -> AshTheme.colors.background
                                kind == item.second -> AshTheme.colors.statusSuccess.copy(alpha = 0.2f)
                                choice == kind -> AshTheme.colors.statusError.copy(alpha = 0.2f)
                                else -> AshTheme.colors.background
                            }
                        )
                        .border(1.2.dp, AshTheme.colors.border, RoundedCornerShape(10.dp))
                        .clickable { choice = kind }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (kind == "Passive") "👁️ Passive" else "⚡ Active", fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                }
            }
        }
        if (choice != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (choice == item.second) "✅ ${item.third}" else "❌ Actually ${item.second}: ${item.third}",
                color = if (choice == item.second) AshTheme.colors.statusSuccess else AshTheme.colors.statusError,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { idx = (idx + 1) % items.size }, colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.brand)) {
                Text("Next Threat ➔", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (showDetails) {
            Spacer(Modifier.height(8.dp))
            AshMono("X.800 Taxonomy: Passive = Release of contents | Active = Masquerade, Replay, Modification, DoS", fontSize = 11.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 3. RAIL FENCE TRANSPOSITION & LSB STEGANOGRAPHY ──────────────

@Composable
private fun TranspositionStegoTryContent(showDetails: Boolean) {
    var msg by remember { mutableStateOf("HELLOASH") }
    var rails by remember { mutableIntStateOf(3) }
    var secretBit by remember { mutableIntStateOf(1) }

    val clean = msg.uppercase().filter { it.isLetterOrDigit() }.ifEmpty { "HELLO" }
    val railRows = List(rails) { StringBuilder() }
    var row = 0
    var dir = 1
    for (ch in clean) {
        railRows[row].append(ch)
        if (rails > 1) {
            if (row == 0) dir = 1 else if (row == rails - 1) dir = -1
            row += dir
        }
    }
    val cipher = railRows.joinToString("") { it.toString() }
    val pixelBinary = if (secretBit == 1) "1101011[1]" else "1101011[0]"

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: RAIL FENCE ZIG-ZAG + LSB PIXEL BIT", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp)).padding(10.dp)) {
            BasicTextField(value = msg, onValueChange = { if (it.length <= 14) msg = it }, textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 15.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(8.dp))
        Text("Rails: $rails", fontWeight = FontWeight.Bold, color = AshTheme.colors.ink, fontSize = 13.sp)
        Slider(value = rails.toFloat(), onValueChange = { rails = it.toInt().coerceIn(2, 4) }, valueRange = 2f..4f, steps = 1, colors = SliderDefaults.colors(thumbColor = AshTheme.colors.brand, activeTrackColor = AshTheme.colors.brand))

        railRows.forEachIndexed { idx, sb ->
            AshMono("Rail ${idx + 1}: ${sb.toString().toList().joinToString(" ")}", fontSize = 13.sp, color = AshTheme.colors.ink)
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.accentContainer).padding(10.dp)) {
            AshMono("Ciphertext: $cipher", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AshTheme.colors.brand)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Stego Blue Channel LSB:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                AshMono(pixelBinary, fontSize = 13.sp, color = AshTheme.colors.brand)
            }
            Button(onClick = { secretBit = 1 - secretBit }, colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.brand)) {
                Text("Flip Bit ($secretBit)", color = Color.White, fontSize = 12.sp)
            }
        }
        if (showDetails) {
            Spacer(Modifier.height(6.dp))
            AshMono("1/256 brightness shift (214 vs 215) is invisible to human eyes.", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 4. PRIME SIEVE OF ERATOSTHENES ───────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrimeSieveTryContent(showDetails: Boolean) {
    val eliminated = remember { mutableStateListOf<Int>() }
    val primes = listOf(2, 3, 5, 7)

    fun toggleFactor(p: Int) {
        for (n in (p * 2)..30 step p) {
            if (!eliminated.contains(n)) eliminated.add(n)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: TAP A PRIME TO SIEVE OUT COMPOSITES", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            primes.forEach { p ->
                Button(onClick = { toggleFactor(p) }, colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.brand)) {
                    Text("×$p", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Button(onClick = { eliminated.clear() }, colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.border)) {
                Text("Reset", color = AshTheme.colors.ink, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (2..30).forEach { n ->
                val isCrossed = eliminated.contains(n)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCrossed) AshTheme.colors.statusError.copy(alpha = 0.15f) else AshTheme.colors.statusSuccess.copy(alpha = 0.16f))
                        .border(1.dp, if (isCrossed) AshTheme.colors.border else AshTheme.colors.statusSuccess, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCrossed) "×" else "$n",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCrossed) AshTheme.colors.subtle else AshTheme.colors.ink
                    )
                }
            }
        }
        if (showDetails) {
            Spacer(Modifier.height(10.dp))
            AshMono("Remaining uncrossed numbers after ×2, ×3, ×5 are all primes <= 30!", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 5. DISCRETE LOGARITHM EXPLORER ───────────────────────────────

@Composable
private fun DiscreteLogTryContent(showDetails: Boolean) {
    var x by remember { mutableIntStateOf(4) }
    val g = 3L
    val p = 17L
    val y = modPow(g, x.toLong(), p)

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: DISCRETE LOG JUMPS (g=3, mod p=17)", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Text("Secret Exponent x = $x", fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
        Slider(value = x.toFloat(), onValueChange = { x = it.toInt() }, valueRange = 1f..16f, colors = SliderDefaults.colors(thumbColor = AshTheme.colors.brand, activeTrackColor = AshTheme.colors.brand))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(AshTheme.colors.accentContainer).padding(12.dp)) {
            AshMono("3^$x mod 17 = $y", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AshTheme.colors.brand)
        }
        Spacer(Modifier.height(8.dp))
        Text("Notice how the output jumps erratically even when x increases by +1! That's why reversing y back to x is hard.", fontSize = 13.sp, color = AshTheme.colors.ink)
        if (showDetails) {
            Spacer(Modifier.height(6.dp))
            AshMono("For 2048-bit p, finding x from y = g^x mod p takes longer than the age of the universe.", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 6. DIGITAL SIGNATURE & TAMPER LAB ────────────────────────────

@Composable
private fun SignVerifyTryContent(showDetails: Boolean) {
    var message by remember { mutableStateOf("Pay Bob \$100") }
    var signedHash by remember { mutableStateOf<String?>(null) }
    var tampered by remember { mutableStateOf(false) }

    val transmittedMsg = if (tampered) "$message [EDITED -> \$999]" else message
    val currentHash = SafeCryptoEngine.safeSha256(transmittedMsg).getOrDefault("0000").take(16)
    val isValid = signedHash != null && signedHash == currentHash

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: SIGN DOCUMENT & SIMULATE TAMPERING", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp)).padding(10.dp)) {
            BasicTextField(value = message, onValueChange = { message = it; signedHash = null; tampered = false }, textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 15.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { signedHash = SafeCryptoEngine.safeSha256(message).getOrDefault("0000").take(16); tampered = false }, colors = ButtonDefaults.buttonColors(containerColor = AshTheme.colors.brand)) {
                Text("1. Sign (Priv Key)", color = Color.White, fontSize = 12.sp)
            }
            Button(onClick = { tampered = !tampered }, enabled = signedHash != null, colors = ButtonDefaults.buttonColors(containerColor = if (tampered) AshTheme.colors.statusError else AshTheme.colors.border)) {
                Text(if (tampered) "Undo Tamper" else "2. Tamper!", color = if (tampered) Color.White else AshTheme.colors.ink, fontSize = 12.sp)
            }
        }
        if (signedHash != null) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (isValid) AshTheme.colors.statusSuccess.copy(alpha = 0.16f) else AshTheme.colors.statusError.copy(alpha = 0.16f)).padding(12.dp)
            ) {
                Column {
                    Text(
                        text = if (isValid) "✅ Signature VERIFIED (Public Key)" else "🚨 Signature INVALID! Document tampered!",
                        fontWeight = FontWeight.Black,
                        color = if (isValid) AshTheme.colors.statusSuccess else AshTheme.colors.statusError
                    )
                    if (showDetails) {
                        Spacer(Modifier.height(4.dp))
                        AshMono("Signed Hash: $signedHash\nCurrent Hash: $currentHash", fontSize = 11.5.sp, color = AshTheme.colors.ink)
                    }
                }
            }
        }
    }
}

// ── 7. IPSEC TRANSPORT VS TUNNEL INSPECTOR ───────────────────────

@Composable
private fun IpsecTryContent(showDetails: Boolean) {
    var tunnelMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: SWITCH IPSEC ENCAPSULATION MODE", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(false to "Transport Mode", true to "Tunnel Mode (VPN)").forEach { (isTunnel, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (tunnelMode == isTunnel) AshTheme.colors.brand else AshTheme.colors.background)
                        .border(1.2.dp, AshTheme.colors.brand, RoundedCornerShape(10.dp))
                        .clickable { tunnelMode = isTunnel }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (tunnelMode == isTunnel) Color.White else AshTheme.colors.ink)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            if (tunnelMode) {
                PacketHeaderChip("New IP", encrypted = false, modifier = Modifier.weight(1f))
                PacketHeaderChip("ESP", encrypted = false, modifier = Modifier.weight(0.7f))
                PacketHeaderChip("🔒 Orig IP", encrypted = true, modifier = Modifier.weight(1f))
                PacketHeaderChip("🔒 TCP+Data", encrypted = true, modifier = Modifier.weight(1.2f))
            } else {
                PacketHeaderChip("Orig IP", encrypted = false, modifier = Modifier.weight(1f))
                PacketHeaderChip("ESP", encrypted = false, modifier = Modifier.weight(0.7f))
                PacketHeaderChip("🔒 TCP+Data", encrypted = true, modifier = Modifier.weight(1.5f))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (tunnelMode) "Tunnel Mode hides your real internal IP addresses inside an armored VPN envelope."
            else "Transport Mode keeps original IP headers visible so routers can deliver host-to-host.",
            fontSize = 13.sp,
            color = AshTheme.colors.ink
        )
        if (showDetails) {
            Spacer(Modifier.height(6.dp))
            AshMono("ESP = Encapsulating Security Payload (Confidentiality + Integrity).", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

@Composable
private fun PacketHeaderChip(label: String, encrypted: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (encrypted) AshTheme.colors.statusSuccess.copy(alpha = 0.2f) else AshTheme.colors.accentContainer)
            .border(1.dp, if (encrypted) AshTheme.colors.statusSuccess else AshTheme.colors.brand, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink, textAlign = TextAlign.Center)
    }
}

// ── 8. FIREWALL RULE SIMULATOR ───────────────────────────────────

@Composable
private fun FirewallTryContent(showDetails: Boolean) {
    val packets = remember { listOf("HTTPS (TCP :443)" to 443, "Telnet (TCP :23)" to 23, "SSH (TCP :22)" to 22, "Malware (TCP :4444)" to 4444) }
    var selectedPacketIdx by remember { mutableIntStateOf(0) }
    var allowHttps by remember { mutableStateOf(true) }
    var denyTelnet by remember { mutableStateOf(true) }
    var allowSsh by remember { mutableStateOf(false) }

    val (pktName, port) = packets[selectedPacketIdx]
    val allowed = when (port) {
        443 -> allowHttps
        23 -> !denyTelnet
        22 -> allowSsh
        else -> false // Default Deny
    }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: TEST PACKETS AGAINST FIREWALL RULES", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            packets.forEachIndexed { idx, (name, p) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedPacketIdx == idx) AshTheme.colors.brand else AshTheme.colors.background)
                        .border(1.dp, AshTheme.colors.border, RoundedCornerShape(8.dp))
                        .clickable { selectedPacketIdx = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(":$p", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedPacketIdx == idx) Color.White else AshTheme.colors.ink)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        listOf(
            Triple("Rule 1: Port 443 (HTTPS)", allowHttps) { allowHttps = !allowHttps },
            Triple("Rule 2: Block Port 23 (Telnet)", denyTelnet) { denyTelnet = !denyTelnet },
            Triple("Rule 3: Port 22 (SSH)", allowSsh) { allowSsh = !allowSsh }
        ).forEach { (label, state, toggle) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.background).clickable { toggle() }.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                Text(if (state) "ON" else "OFF", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (state) AshTheme.colors.statusSuccess else AshTheme.colors.subtle)
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (allowed) AshTheme.colors.statusSuccess.copy(alpha = 0.16f) else AshTheme.colors.statusError.copy(alpha = 0.16f)).padding(12.dp)
        ) {
            Text(
                text = if (allowed) "✅ $pktName ALLOWED through firewall" else "🚫 $pktName DROPPED (Blocked or Default Deny)",
                fontWeight = FontWeight.Black,
                color = if (allowed) AshTheme.colors.statusSuccess else AshTheme.colors.statusError
            )
        }
        if (showDetails) {
            Spacer(Modifier.height(6.dp))
            AshMono("Rule 4 (Implicit): DENY ALL unmatched traffic.", fontSize = 11.5.sp, color = AshTheme.colors.subtle)
        }
    }
}

// ── 9. SQL INJECTION SANDBOX ─────────────────────────────────────

@Composable
private fun SqliTryContent(showDetails: Boolean) {
    var payload by remember { mutableStateOf("' OR '1'='1") }
    var usePrepared by remember { mutableStateOf(false) }
    val isInjected = !usePrepared && (payload.contains("'") || payload.lowercase().contains(" or "))

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AshTheme.colors.surface).border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Text("TRY: TEST SQL INJECTION VS PREPARED STATEMENT", style = AshTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AshTheme.colors.brand)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("alice", "' OR '1'='1", "admin'--").forEach { preset ->
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(8.dp)).clickable { payload = preset }.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(preset, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AshTheme.colors.ink)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.background).border(1.dp, AshTheme.colors.border, RoundedCornerShape(8.dp)).padding(10.dp)) {
            BasicTextField(value = payload, onValueChange = { payload = it }, textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { usePrepared = !usePrepared }, colors = ButtonDefaults.buttonColors(containerColor = if (usePrepared) AshTheme.colors.statusSuccess else AshTheme.colors.statusError)) {
            Text(if (usePrepared) "Defense: Prepared Statement (?) ON" else "Mode: Vulnerable String Concat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AshTheme.colors.accentContainer).padding(10.dp)) {
            AshMono(
                text = if (usePrepared) "SELECT * FROM users WHERE name = ?  [param: \"$payload\"]"
                else "SELECT * FROM users WHERE name = '$payload'",
                fontSize = 12.sp,
                color = AshTheme.colors.ink
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isInjected) "🚨 BREACH! Query logic altered — attacker logged in without password!"
            else if (usePrepared) "🛡️ SAFE! Database treats '$payload' strictly as plain text."
            else "✅ Normal query executed.",
            fontWeight = FontWeight.Bold,
            color = if (isInjected) AshTheme.colors.statusError else AshTheme.colors.statusSuccess,
            fontSize = 13.sp
        )
        if (showDetails) {
            Spacer(Modifier.height(6.dp))
            AshMono("OWASP A03: Always separate SQL code from user-supplied parameters.", fontSize = 11.sp, color = AshTheme.colors.subtle)
        }
    }
}
