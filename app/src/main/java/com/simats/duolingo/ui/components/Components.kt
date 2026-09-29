package com.simats.duolingo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.ui.theme.*

// ─── Duolingo 3-D Push Button ─────────────────────────────────────────────────
@Composable
fun DuolingoButton(
    text: String,
    backgroundColor: Color,
    shadowColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(80),
        label = "topOffset"
    )

    Box(
        modifier = modifier
            .height(50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow bottom layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        // Top face layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = topOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─── Custom input text field ───────────────────────────────────────────────────
@Composable
fun DuoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isFocused: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = DuolingoPlaceholder, fontSize = 15.sp)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = DuolingoInputBg,
            unfocusedContainerColor = DuolingoInputBg,
            focusedTextColor        = Color.White,
            unfocusedTextColor      = Color.White,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor             = DuolingoBlue,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isFocused) 2.dp else 1.5.dp,
                color = if (isFocused) DuolingoBlue else DuolingoInputBorder,
                shape = RoundedCornerShape(14.dp)
            )
    )
}

// ─── Custom secure / password field ───────────────────────────────────────────
@Composable
fun DuoSecureField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = DuolingoPlaceholder, fontSize = 15.sp)
        },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = DuolingoInputBg,
            unfocusedContainerColor = DuolingoInputBg,
            focusedTextColor        = Color.White,
            unfocusedTextColor      = Color.White,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor             = DuolingoBlue,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isFocused) 2.dp else 1.5.dp,
                color = if (isFocused) DuolingoBlue else DuolingoInputBorder,
                shape = RoundedCornerShape(14.dp)
            )
    )
}

// ─── Small chip / badge ────────────────────────────────────────────────────────
@Composable
fun StatChip(
    emoji: String,
    value: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 17.sp)
        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
        )
    }
}
