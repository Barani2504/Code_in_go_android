package com.simats.codeingo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Phoenix Bird Life shape system — mirrors iOS cornerRadius values (pt → dp 1:1). */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Convenience aliases used by components
val CardShape        = RoundedCornerShape(16.dp)
val ButtonShape      = RoundedCornerShape(16.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val DialogShape      = RoundedCornerShape(20.dp)
val PillShape        = RoundedCornerShape(50)
val LiquidCardShape  = RoundedCornerShape(22.dp)  // iOS liquidGlassCard default
val WorldCardShape   = RoundedCornerShape(18.dp)  // world entry cards
