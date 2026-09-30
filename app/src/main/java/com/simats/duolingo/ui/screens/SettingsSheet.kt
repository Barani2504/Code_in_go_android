package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class SettingsTab(val title: String) {
    PREFERENCES("Preferences"),
    NOTIFICATIONS("Notifications"),
    COURSES("Courses"),
    ACCOUNT("Account")
}

@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
) {
    var activeTab by remember { mutableStateOf(SettingsTab.PREFERENCES) }
    val coroutineScope = rememberCoroutineScope()

    // Preferences toggles
    var soundEffects by remember { mutableStateOf(true) }
    var animations by remember { mutableStateOf(true) }
    var motivationalMessages by remember { mutableStateOf(true) }
    var listeningExercises by remember { mutableStateOf(true) }

    // Notifications state
    var productUpdates by remember { mutableStateOf(true) }
    var newFollower by remember { mutableStateOf(true) }
    var friendActivity by remember { mutableStateOf(true) }
    var weeklyProgress by remember { mutableStateOf(true) }
    var specialPromotions by remember { mutableStateOf(true) }
    var researchOpportunities by remember { mutableStateOf(true) }
    var practiceReminder by remember { mutableStateOf(true) }
    var practiceTime by remember { mutableStateOf("5 PM") }
    var showTimeMenu by remember { mutableStateOf(false) }

    // Courses state
    var showResetDialog by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (activeTab != SettingsTab.PREFERENCES) {
                    Row(
                        modifier = Modifier
                            .clickable { activeTab = SettingsTab.PREFERENCES }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("‹", color = DuolingoBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Back", color = DuolingoBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text("✕", color = DuolingoSubtext, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = activeTab.title,
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "DONE",
                    color = DuolingoBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clickable(onClick = onDismiss)
                        .padding(4.dp)
                )
            }

            HorizontalDivider(color = DuolingoInputBorder)

            // Tab Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                when (activeTab) {
                    SettingsTab.PREFERENCES -> {
                        // Lesson Experience
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Lesson experience",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(color = DuolingoInputBorder.copy(alpha = 0.6f))
                            ToggleRow("Sound effects", soundEffects) { soundEffects = it }
                            HorizontalDivider(color = DuolingoInputBorder.copy(alpha = 0.3f))
                            ToggleRow("Animations", animations) { animations = it }
                            HorizontalDivider(color = DuolingoInputBorder.copy(alpha = 0.3f))
                            ToggleRow("Motivational messages", motivationalMessages) { motivationalMessages = it }
                            HorizontalDivider(color = DuolingoInputBorder.copy(alpha = 0.3f))
                            ToggleRow("Listening exercises", listeningExercises) { listeningExercises = it }
                        }

                        // Daily Fire Streak Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔥 Daily Fire Streak",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFF9600).copy(0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${AppState.dayStreak} DAYS",
                                        color = Color(0xFFFF9600),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFF9600))
                                        .clickable { AppState.simulateNextDayOpening() }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+1 Day Streak", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Red.copy(0.15f))
                                        .border(1.dp, Color.Red.copy(0.4f), RoundedCornerShape(12.dp))
                                        .clickable { AppState.simulateMissedDaysReset() }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Reset Streak", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Account Navigation Card
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Account",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                            ) {
                                NavigationRow("Account", AppState.userName) { activeTab = SettingsTab.ACCOUNT }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.4f))
                                NavigationRow("Preferences", "Active") {}
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.4f))
                                NavigationRow("Profile", "@${AppState.userHandle}") { activeTab = SettingsTab.ACCOUNT }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.4f))
                                NavigationRow("Notifications", "") { activeTab = SettingsTab.NOTIFICATIONS }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.4f))
                                NavigationRow("Courses", "") { activeTab = SettingsTab.COURSES }
                            }
                        }

                        // Support Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Support", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                            ) {
                                NavigationRow("Help Center", "") {}
                            }
                        }
                    }

                    SettingsTab.NOTIFICATIONS -> {
                        // General Notifications
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("General", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Email", color = DuolingoSubtext, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = DuolingoInputBorder.copy(0.6f))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                            ) {
                                CheckboxRow("Product updates + learning tips", productUpdates) { productUpdates = it }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.3f))
                                CheckboxRow("New follower", newFollower) { newFollower = it }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.3f))
                                CheckboxRow("Friend activity", friendActivity) { friendActivity = it }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.3f))
                                CheckboxRow("Weekly progress", weeklyProgress) { weeklyProgress = it }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.3f))
                                CheckboxRow("Special promotions", specialPromotions) { specialPromotions = it }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.3f))
                                CheckboxRow("Research participation opportunities", researchOpportunities) { researchOpportunities = it }
                            }
                        }

                        // Daily Reminders
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Daily reminders", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Email", color = DuolingoSubtext, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = DuolingoInputBorder.copy(0.6f))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                            ) {
                                CheckboxRow("Practice reminder", practiceReminder) { practiceReminder = it }
                            }

                            // Time Picker Dropdown
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DuolingoCardBg)
                                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                                        .clickable { showTimeMenu = true }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(practiceTime, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("▾", color = DuolingoSubtext, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                DropdownMenu(
                                    expanded = showTimeMenu,
                                    onDismissRequest = { showTimeMenu = false },
                                    modifier = Modifier.background(DuolingoCardBg)
                                ) {
                                    listOf("9 AM", "12 PM", "5 PM", "7 PM", "9 PM").forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time, color = Color.White) },
                                            onClick = {
                                                practiceTime = time
                                                showTimeMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SettingsTab.COURSES -> {
                        val currentLang = AppState.selectedLanguage
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Courses", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(currentLang?.flagEmoji ?: "🐍", fontSize = 32.sp)
                                Text(
                                    text = currentLang?.name ?: "Python",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "RESET",
                                    color = DuolingoSubtext,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier
                                        .clickable { showResetDialog = true }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }

                    SettingsTab.ACCOUNT -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Account Details", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Name", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(AppState.userName.ifEmpty { "Learner" }, color = DuolingoSubtext, fontSize = 15.sp)
                                }
                                HorizontalDivider(color = DuolingoInputBorder.copy(0.6f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Username", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("@${AppState.userHandle.ifEmpty { "learner" }}", color = DuolingoSubtext, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reset Alert Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Course Progress?", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to reset your progress in this course? This action cannot be undone.", color = DuolingoSubtext) },
                confirmButton = {
                    TextButton(onClick = {
                        showResetDialog = false
                        AppState.unlockedLevelIndices = setOf(1, 7, 13, 18, 23)
                        AppState.activeLevelIndex = 1
                        toastMessage = "Course progress reset successfully!"
                        coroutineScope.launch {
                            delay(2500)
                            toastMessage = null
                        }
                    }) {
                        Text("Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = DuolingoBlue)
                    }
                },
                containerColor = DuolingoCardBg
            )
        }

        // Toast message
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            toastMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg)
                        .border(1.5.dp, DuolingoGreen, RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✓", color = DuolingoGreen, fontWeight = FontWeight.Bold)
                    Text(msg, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DuolingoBlue,
                uncheckedThumbColor = DuolingoSubtext,
                uncheckedTrackColor = DuolingoInputBorder
            )
        )
    }
}

@Composable
private fun CheckboxRow(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) DuolingoBlue else Color.Transparent)
                .border(2.dp, if (isChecked) DuolingoBlue else DuolingoSubtext, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NavigationRow(title: String, detail: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (detail.isNotEmpty()) {
                Text(detail, color = DuolingoSubtext, fontSize = 14.sp)
            }
            Text("›", color = DuolingoSubtext, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
