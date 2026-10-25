package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandTeal

enum class EmblemSize(val dp: Dp, val iconDp: Dp) {
    SMALL(24.dp, 13.dp),
    TOOLBAR(28.dp, 15.dp),
    MEDIUM(38.dp, 20.dp),
    LARGE(56.dp, 30.dp),
    HERO(68.dp, 36.dp)
}

/**
 * Distinctive Custom AI Companion Emblem for Study Buddy.
 * Combines an intelligent geometric node silhouette, dual gradient rings, and a glowing core accent.
 */
@Composable
fun StudyBuddyEmblem(
    size: EmblemSize = EmblemSize.MEDIUM,
    isAnimated: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scalePulse = if (isAnimated) {
        val infiniteTransition = rememberInfiniteTransition(label = "emblem_pulse")
        val animateScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
        animateScale
    } else {
        1.0f
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = BrandTeal
    val accentViolet = Color(0xFF6366F1)

    Box(
        modifier = modifier
            .size(size.dp)
            .scale(scalePulse),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient ring glow
        Canvas(modifier = Modifier.size(size.dp)) {
            val radius = (size.dp.toPx() / 2f) - 1.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = 0.35f),
                        accentViolet.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                radius = radius
            )
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor,
                        accentViolet,
                        primaryColor
                    )
                ),
                radius = radius - 1.dp.toPx(),
                style = Stroke(width = 1.8.dp.toPx())
            )
        }

        // Core geometric node background
        Box(
            modifier = Modifier
                .size(size.dp - 4.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primaryColor,
                            secondaryColor,
                            accentViolet
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Study Buddy AI Companion",
                tint = Color.White,
                modifier = Modifier.size(size.iconDp)
            )
        }
    }
}
