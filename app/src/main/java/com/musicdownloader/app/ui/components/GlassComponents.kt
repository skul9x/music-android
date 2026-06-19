package com.musicdownloader.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val roundedCornerShape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .clip(roundedCornerShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x221E1E2F),
                        Color(0x110F0F1A)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                ),
                shape = roundedCornerShape
            )
    ) {
        content()
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "button_press")

    val roundedCornerShape = RoundedCornerShape(16.dp)
    val gradientBrush = if (enabled) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF8A2387),
                Color(0xFFE94057),
                Color(0xFFF27121)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x338A2387),
                Color(0x33E94057),
                Color(0x33F27121)
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .clip(roundedCornerShape)
            .background(gradientBrush)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 14.dp, horizontal = 24.dp)
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f),
            fontSize = 16.sp,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}
