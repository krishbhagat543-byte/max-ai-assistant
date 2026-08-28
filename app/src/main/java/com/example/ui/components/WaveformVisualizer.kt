package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.models.OrbState
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    orbState: OrbState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val barColor = when (orbState) {
        OrbState.SPEAKING -> NeonPink
        OrbState.LISTENING -> NeonCyan
        OrbState.PROCESSING -> NeonViolet
        else -> Color(0xFF2C3A5A)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        val barCount = 28
        val spacing = 4.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(3.dp.toPx())
        val maxHeight = size.height

        val active = orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING || orbState == OrbState.PROCESSING
        val effectiveAmp = if (active) amplitude.coerceIn(0.15f, 1f) else 0.08f

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val sinVal = sin((i * 0.4f + phase).toDouble()).toFloat()
            val normalizedHeight = (0.2f + 0.8f * Math.abs(sinVal)) * effectiveAmp
            val barH = (normalizedHeight * maxHeight).coerceIn(4.dp.toPx(), maxHeight)
            val y = (maxHeight - barH) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        barColor.copy(alpha = 0.9f),
                        barColor.copy(alpha = 0.4f)
                    ),
                    startY = y,
                    endY = y + barH
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
