package com.simats.duolingo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontFamily
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
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 4.dp else 0.dp,
        animationSpec = tween(80),
        label = "topOffset"
    )

    val actualBg = if (enabled) backgroundColor else DuolingoCardBg
    val actualShadow = if (enabled) shadowColor else DuolingoInputBorder
    val actualText = if (enabled) textColor else DuolingoSubtext

    Box(
        modifier = modifier
            .height(50.dp)
            .clickable(
                enabled = enabled,
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
                .background(actualShadow)
        )
        // Top face layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = topOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(actualBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = actualText,
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
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = DuolingoPlaceholder, fontSize = 15.sp)
        },
        singleLine = true,
        trailingIcon = trailingIcon,
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
                width = if (value.isNotEmpty()) 2.dp else 1.5.dp,
                color = if (value.isNotEmpty()) DuolingoBlue else DuolingoInputBorder,
                shape = RoundedCornerShape(14.dp)
            )
    )
}

// ─── Custom secure / password field with visibility toggle ───────────────────
@Composable
fun DuoSecureField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = DuolingoPlaceholder, fontSize = 15.sp)
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = trailingIcon ?: {
            Text(
                text = if (passwordVisible) "👁" else "👁‍🗨",
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { passwordVisible = !passwordVisible }
                    .padding(8.dp)
            )
        },
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
                width = if (value.isNotEmpty()) 2.dp else 1.5.dp,
                color = if (value.isNotEmpty()) DuolingoBlue else DuolingoInputBorder,
                shape = RoundedCornerShape(14.dp)
            )
    )
}

// ─── Official Google 4-color Vector 'G' Logo ──────────────────────────────────
@Composable
fun GoogleLogoView(size: Dp = 20.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val r = (w.coerceAtMost(h) / 2f)
        val innerR = r * 0.52f

        val blueColor = Color(66, 133, 244)
        val greenColor = Color(52, 168, 83)
        val yellowColor = Color(251, 188, 5)
        val redColor = Color(234, 67, 53)

        val outerRect = Rect(center.x - r, center.y - r, center.x + r, center.y + r)
        val innerRect = Rect(center.x - innerR, center.y - innerR, center.x + innerR, center.y + innerR)

        // 1. Blue arc + horizontal bar
        val bluePath = Path().apply {
            arcTo(outerRect, -15f, 60f, false)
            arcTo(innerRect, 45f, -60f, false)
            close()
        }
        drawPath(bluePath, blueColor)
        drawRect(
            color = blueColor,
            topLeft = Offset(center.x, center.y - innerR * 0.45f),
            size = Size(r, (r - innerR) * 0.95f)
        )

        // 2. Green arc
        val greenPath = Path().apply {
            arcTo(outerRect, 45f, 85f, false)
            arcTo(innerRect, 130f, -85f, false)
            close()
        }
        drawPath(greenPath, greenColor)

        // 3. Yellow arc
        val yellowPath = Path().apply {
            arcTo(outerRect, 130f, 90f, false)
            arcTo(innerRect, 220f, -90f, false)
            close()
        }
        drawPath(yellowPath, yellowColor)

        // 4. Red arc
        val redPath = Path().apply {
            arcTo(outerRect, 220f, 125f, false)
            arcTo(innerRect, 345f, -125f, false)
            close()
        }
        drawPath(redPath, redColor)
    }
}

// ─── Facebook 'f' Vector Logo ─────────────────────────────────────────────────
@Composable
fun FacebookLogoView(size: Dp = 20.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(24, 119, 242)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "f",
            color = Color.White,
            fontSize = (size.value * 0.75f).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.offset(x = 1.dp, y = (-1).dp)
        )
    }
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
