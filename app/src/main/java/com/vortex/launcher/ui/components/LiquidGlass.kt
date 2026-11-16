package com.vortex.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Liquid Glass без тяжёлого blur: полупрозрачная подложка + градиентная белая обводка 1dp.
 * Дёшево для слабого железа (Сбербокс): никаких RenderEffect на больших областях.
 */
fun Modifier.liquidGlass(shape: Shape, focused: Boolean = false): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (focused) 0.30f else 0.14f),
                Color.White.copy(alpha = if (focused) 0.16f else 0.06f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = if (focused) 1f else 0.60f),
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = if (focused) 0.80f else 0.40f)
            )
        ),
        shape = shape
    )

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    focusRequester: FocusRequester? = null,
    onFocused: (() -> Unit)? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.08f,
        focusRequester = focusRequester,
        onFocused = onFocused
    ) { focused ->
        Row(
            modifier = Modifier
                .liquidGlass(RoundedCornerShape(26.dp), focused)
                .padding(horizontal = 26.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun GlassCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    focusRequester: FocusRequester? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.14f,
        focusRequester = focusRequester
    ) { focused ->
        Box(
            modifier = Modifier
                .size(size)
                .liquidGlass(CircleShape, focused),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.46f))
        }
    }
}
