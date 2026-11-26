package com.simats.codeingo.ui.components.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.domain.guide.CryptoGlossary
import com.simats.codeingo.domain.guide.GlossaryTerm
import com.simats.codeingo.domain.guide.PhoenixGuideDirector
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.AshType

/**
 * Screen context descriptor passed into AskAshBottomSheet.
 */
data class ScreenGuideContext(
    val screenId: String,
    val screenTitle: String,
    val whatIsThis: String,
    val whatNext: String,
    val tourScriptId: String? = null
)

object ScreenGuideCatalog {
    val HOME = ScreenGuideContext(
        screenId = "HOME",
        screenTitle = "Learning Journey Path",
        whatIsThis = "This is your main quest map. It takes you realm by realm through the Anna University CS8792 syllabus and modern security practices.",
        whatNext = "Tap the Continue button or your next glowing node to begin a bite-sized lesson or interactive lab.",
        tourScriptId = "HOME_TOUR"
    )

    val VISUALIZER = ScreenGuideContext(
        screenId = "VISUALIZER",
        screenTitle = "Crypto Visualizer Labs",
        whatIsThis = "An interactive laboratory where you can see mathematical transformations, key expansions, and cipher rounds happen step-by-step.",
        whatNext = "Select any lab, enter your own plaintext or key, and tap 'Step' to watch the encryption dance.",
        tourScriptId = "VISUALIZER_TOUR"
    )

    val ARENA = ScreenGuideContext(
        screenId = "ARENA",
        screenTitle = "Threat Arenas",
        whatIsThis = "Realistic security attack simulations: brute force attacks, MITM packet sniffers, hash collisions, and protocol exploits.",
        whatNext = "Choose an arena to inspect vulnerabilities, crack intercepted payloads, and patch the breach.",
        tourScriptId = "ARENA_TOUR"
    )

    val PRACTICE = ScreenGuideContext(
        screenId = "PRACTICE",
        screenTitle = "Reforge Practice",
        whatIsThis = "Your memory workout gym. Questions you found difficult or haven't practiced recently resurface here so you never forget them.",
        whatNext = "Start a 5-question quick drill to earn XP and polish your security instincts.",
        tourScriptId = "PRACTICE_TOUR"
    )

    val LEADERBOARD = ScreenGuideContext(
        screenId = "LEADERBOARD",
        screenTitle = "Cryptographic Leagues",
        whatIsThis = "A friendly leaderboard comparing your weekly study XP with fellow security learners around the globe.",
        whatNext = "Complete lessons and arena drills to rise through the leagues: Copper, Silver, Gold, and Phoenix Champion!",
        tourScriptId = "LEADERBOARD_TOUR"
    )

    val SHOP = ScreenGuideContext(
        screenId = "SHOP",
        screenTitle = "Gem Shop & Sanctuary",
        whatIsThis = "Where you spend the gems you earn by learning. Buy shields to protect your streak or cosmetics for Ash.",
        whatNext = "Make sure you keep at least one Streak Freeze in your inventory for busy days!",
        tourScriptId = "SHOP_TOUR"
    )

    val PROFILE = ScreenGuideContext(
        screenId = "PROFILE",
        screenTitle = "Security Dossier",
        whatIsThis = "Your cryptography portfolio showing completed chapters, mastered algorithms, total XP, and earned certificates.",
        whatNext = "Share your achievements with friends or review your strongest and weakest topics.",
        tourScriptId = "PROFILE_TOUR"
    )
}

/**
 * Permanent "Ask Ash" button composable.
 */
@Composable
fun AskAshButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(AshTheme.colors.brand.copy(alpha = 0.14f))
            .border(1.2.dp, AshTheme.colors.brand.copy(alpha = 0.75f), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ash 🪶",
            style = AshType.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = AshTheme.colors.brand
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Permanent contextual Bottom Sheet opened by AskAshButton.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAshBottomSheet(
    guideContext: ScreenGuideContext,
    userId: String?,
    onReplayTour: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val filteredTerms = remember(searchQuery) {
        CryptoGlossary.searchTerms(searchQuery)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AshTheme.colors.bg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Ash Mascot + Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.phoenix),
                        contentDescription = "Ash",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ash's Field Guide",
                            style = AshType.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = AshTheme.colors.ink
                        )
                        Text(
                            text = guideContext.screenTitle,
                            style = AshType.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = AshTheme.colors.brand
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AshTheme.colors.inkMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 1: What is this screen?
            GuideInfoCard(
                icon = "🗺️",
                title = "What is this screen?",
                body = guideContext.whatIsThis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 2: What should I do next?
            GuideInfoCard(
                icon = "🎯",
                title = "What should I do next?",
                body = guideContext.whatNext
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tour Replay Button if available
            if (guideContext.tourScriptId != null) {
                AshButton(
                    text = "Replay Screen Tour 🔄",
                    onClick = {
                        onDismiss()
                        onReplayTour(guideContext.tourScriptId)
                    },
                    style = AshButtonStyle.OUTLINE,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 3: Glossary Search
            Text(
                text = "Glossary & Crypto Terms",
                style = AshType.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                ),
                color = AshTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search terms (e.g. cipher, hash, nonce)...",
                        style = AshType.bodyMedium.copy(fontSize = 13.sp),
                        color = AshTheme.colors.inkMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AshTheme.colors.inkMuted
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AshTheme.colors.brand,
                    unfocusedBorderColor = AshTheme.colors.accent.copy(alpha = 0.4f),
                    focusedContainerColor = AshTheme.colors.surface,
                    unfocusedContainerColor = AshTheme.colors.surface,
                    focusedTextColor = AshTheme.colors.ink,
                    unfocusedTextColor = AshTheme.colors.ink
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search results (up to 4 terms)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredTerms.take(4).forEach { term ->
                    GlossaryTermCard(term = term)
                }
            }
        }
    }
}

@Composable
private fun GuideInfoCard(
    icon: String,
    title: String,
    body: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.dp, AshTheme.colors.accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = AshType.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    ),
                    color = AshTheme.colors.ink
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    style = AshType.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    color = AshTheme.colors.inkMuted
                )
            }
        }
    }
}

@Composable
private fun GlossaryTermCard(
    term: GlossaryTerm
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AshTheme.colors.surface)
            .border(1.dp, AshTheme.colors.accent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = term.term,
                    style = AshType.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = AshTheme.colors.ink
                )
                Text(
                    text = if (expanded) "▲" else "▼",
                    fontSize = 11.sp,
                    color = AshTheme.colors.inkMuted
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = term.shortDefinition,
                        style = AshType.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = AshTheme.colors.inkMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Example: ${term.example}",
                        style = AshType.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = AshTheme.colors.brand
                    )
                }
            }
        }
    }
}
