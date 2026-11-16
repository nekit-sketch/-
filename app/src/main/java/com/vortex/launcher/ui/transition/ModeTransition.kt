package com.vortex.launcher.ui.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.SkinType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Классика -> Гейминг: из чёрно-красной дыры вылетает белый джойстик, под ним "Vortex Gaming".
 * Гейминг -> Классика: из чёрной дыры вылетает надпись "Vortex".
 * onMidpoint вызывается, когда экран полностью закрыт: за ним меняется режим. Затем плавное проявление.
 */
@Composable
fun ModeTransitionOverlay(
    target: SkinType,
    onMidpoint: () -> Unit,
    onFinished: () -> Unit
) {
    val hole = remember { Animatable(0f) }
    val objectScale = remember { Animatable(0f) }
    val objectRotation = remember { Animatable(-25f) }
    val textAlpha = remember { Animatable(0f) }
    val overlayAlpha = remember { Animatable(1f) }
    val midpoint by rememberUpdatedState(onMidpoint)
    val finished by rememberUpdatedState(onFinished)

    val spin = rememberInfiniteTransition(label = "spin")
    val angle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "angle"
    )

    LaunchedEffect(target) {
        hole.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        coroutineScope {
            launch { objectScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
            if (target == SkinType.GAMING) {
                launch { objectRotation.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)) }
            }
            launch {
                delay(450)
                textAlpha.animateTo(1f, tween(500))
            }
        }
        delay(700)
        midpoint()
        overlayAlpha.animateTo(0f, tween(700))
        finished()
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = overlayAlpha.value }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val h = hole.value
            drawRect(Color.Black.copy(alpha = (h * 1.5f).coerceAtMost(1f)))
            val c = center
            val radius = size.maxDimension * 0.75f * h
            if (radius > 1f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Color.Black,
                        0.45f to Color.Black,
                        0.75f to Color(0xFF5A000F),
                        0.92f to Color(0xFFB3001B).copy(alpha = 0.5f),
                        1f to Color.Transparent,
                        center = c,
                        radius = radius
                    ),
                    radius = radius,
                    center = c
                )
                val ring = radius * 0.62f
                for (k in 0 until 3) {
                    drawArc(
                        color = Color(0xFFFF2A3D).copy(alpha = 0.55f * h),
                        startAngle = angle + k * 120f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(c.x - ring, c.y - ring),
                        size = Size(ring * 2f, ring * 2f),
                        style = Stroke(width = 5f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (target == SkinType.GAMING) {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            val s = objectScale.value
                            scaleX = s
                            scaleY = s
                            rotationZ = objectRotation.value
                            translationY = (1f - s.coerceAtMost(1f)) * 160f
                        }
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Vortex Gaming",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = textAlpha.value
                        translationY = (1f - textAlpha.value) * 40f
                    }
                )
            } else {
                Text(
                    text = "Vortex",
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    modifier = Modifier.graphicsLayer {
                        val s = objectScale.value
                        scaleX = s
                        scaleY = s
                        alpha = textAlpha.value
                    }
                )
            }
        }
    }
}
