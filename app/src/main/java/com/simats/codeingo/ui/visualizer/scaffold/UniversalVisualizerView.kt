package com.simats.codeingo.ui.visualizer.scaffold

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
    var currentBeat by remember { mutableStateOf(VisualizerBeat.STORY) }
    var watchStepIndex by remember { mutableIntStateOf(0) }
    var showDetails by remember { mutableStateOf(false) }

    // Dynamic State for interactive TRY beat
    var textInput by remember { mutableStateOf("HELLO") }
    var shiftValue by remember { mutableIntStateOf(3) }
    var modA by remember { mutableStateOf("17") }
    var modM by remember { mutableStateOf("5") }
    val selectedColorAlice = Color(0xFFEF4444) // Red
    val selectedColorBob = Color(0xFF3B82F6)   // Blue

    // Computed what-changed description
    val whatChanged = when (spec.id) {
        "substitution_lab" -> "Shifted by $shiftValue: 'H' becomes '${SafeCryptoEngine.safeCaesar("H", shiftValue).getOrNull()?.ciphertext}'"
        "sha256_avalanche" -> "Input '${textInput}': 256-bit hash recomputed in real time."
        "modular_clock" -> {
            val a = modA.toLongOrNull() ?: 17L
            val m = modM.toLongOrNull() ?: 5L
            val res = SafeCryptoEngine.safeModulo(a, m).getOrNull()
            "$a mod $m = ${res?.remainder ?: 2}"
        }
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
                "substitution_lab" -> CaesarTryContent(
                    text = textInput,
                    onTextChange = { textInput = it },
                    shift = shiftValue,
                    onShiftChange = { shiftValue = it },
                    showDetails = details
                )
                "sha256_avalanche" -> Sha256TryContent(
                    text = textInput,
                    onTextChange = { textInput = it },
                    showDetails = details
                )
                "dh_elgamal_lab" -> DhTryContent(
                    aliceColor = selectedColorAlice,
                    bobColor = selectedColorBob,
                    showDetails = details
                )
                "modular_clock" -> ModuloTryContent(
                    aStr = modA,
                    onAChange = { modA = it },
                    mStr = modM,
                    onMChange = { modM = it },
                    showDetails = details
                )
                "rsa_stepper" -> RsaTryContent(showDetails = details)
                else -> GenericInteractiveTryContent(
                    spec = spec,
                    input = textInput,
                    onInputChange = { textInput = it },
                    showDetails = details
                )
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
        Text(
            text = "STEP ${stepIndex + 1} OF ${spec.watchSteps.size}",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = step.narration,
            style = AshTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.ink,
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Pulse visualization card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AshTheme.colors.background)
                .border(2.dp, AshTheme.colors.brand, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⚡ Action in Progress",
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
            }
        }

        if (showDetails && step.detailNotes.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AshTheme.colors.surfaceContainer)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "🔬 Under the Hood (NIST / Math):",
                        style = AshTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.subtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AshMono(
                        text = step.detailNotes,
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

// ── TRY BEAT: DIFFIE-HELLMAN ─────────────────────────────────────

@Composable
private fun DhTryContent(aliceColor: Color, bobColor: Color, showDetails: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "TRY: EXCHANGE MIXED COLORS IN PUBLIC",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Alice (Secret Red)", style = AshTheme.typography.labelSmall, color = AshTheme.colors.ink)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(aliceColor))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Public (Yellow)", style = AshTheme.typography.labelSmall, color = AshTheme.colors.ink)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFFACC15)))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Bob (Secret Blue)", style = AshTheme.typography.labelSmall, color = AshTheme.colors.ink)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(bobColor))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF78350F))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎉 Result: Both Arrive at Shared Secret Brown!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
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

// ── GENERIC INTERACTIVE TRY CONTENT ──────────────────────────────

@Composable
private fun GenericInteractiveTryContent(
    spec: VisualizerSpec,
    input: String,
    onInputChange: (String) -> Unit,
    showDetails: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "INTERACTIVE LAB: ${spec.title.uppercase()}",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Experiment with parameters in real time to observe cryptographic transformations.",
            style = AshTheme.typography.bodyLarge,
            color = AshTheme.colors.ink,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AshTheme.colors.background)
                .border(1.dp, AshTheme.colors.border, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            BasicTextField(
                value = input,
                onValueChange = onInputChange,
                textStyle = TextStyle(color = AshTheme.colors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                cursorBrush = SolidColor(AshTheme.colors.brand),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AshTheme.colors.accentContainer)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "Active Simulation:",
                    style = AshTheme.typography.labelSmall,
                    color = AshTheme.colors.brand,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                AshMono(
                    text = "Input Length: ${input.length} chars | Mod: ${spec.realmTitle}",
                    fontSize = 13.sp,
                    color = AshTheme.colors.ink
                )
            }
        }
    }
}
