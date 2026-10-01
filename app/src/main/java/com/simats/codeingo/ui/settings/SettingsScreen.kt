package com.simats.codeingo.ui.settings

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var soundEffects by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var reminders by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SubtextGray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "SETTINGS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.padding(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preferences Section
        Text(
            text = "PREFERENCES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = SubtextGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val shape = RoundedCornerShape(16.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
        ) {
            SettingsToggleRow(
                title = "Sound Effects",
                checked = soundEffects,
                onCheckedChange = { soundEffects = it }
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(InputBorder.copy(alpha = 0.6f)))
            SettingsToggleRow(
                title = "Haptic Feedback",
                checked = haptics,
                onCheckedChange = { haptics = it }
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(InputBorder.copy(alpha = 0.6f)))
            SettingsToggleRow(
                title = "Daily Practice Reminders",
                checked = reminders,
                onCheckedChange = { reminders = it }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Account Section
        Text(
            text = "ACCOUNT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = SubtextGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Username", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "vishal_rao", fontSize = 14.sp, color = SubtextGray)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(InputBorder.copy(alpha = 0.6f)))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Email", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "vishal.rao@gmail.com", fontSize = 14.sp, color = SubtextGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Log out button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
                .clickable { onLogout() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LOG OUT",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = DuolingoRed
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DuolingoGreen,
                uncheckedThumbColor = SubtextGray,
                uncheckedTrackColor = Color(0xFF1B2631)
            )
        )
    }
}
