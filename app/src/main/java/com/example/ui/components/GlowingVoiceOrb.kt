package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.models.OrbState
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlowingVoiceOrb(
    orbState: OrbState,
    amplitude: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animations")

    // Slow rotation for quantum orbital rings
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Reverse rotation
    val reverseRotationAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverse_rotation"
    )

    // Breathing pulse
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Core color resolution based on state
    val primaryColor = when (orbState) {
        OrbState.DISCONNECTED -> Color(0xFF3B4E76)
        OrbState.CONNECTING -> NeonCyan
        OrbState.LISTENING -> NeonCyan
        OrbState.PROCESSING -> NeonViolet
        OrbState.SPEAKING -> NeonPink
        OrbState.EXECUTING_ACTION -> NeonGreen
    }

    val secondaryColor = when (orbState) {
        OrbState.DISCONNECTED -> Color(0xFF1E2842)
        OrbState.CONNECTING -> NeonViolet
        OrbState.LISTENING -> NeonGreen
        OrbState.PROCESSING -> NeonPink
        OrbState.SPEAKING -> NeonViolet
        OrbState.EXECUTING_ACTION -> NeonAmber
    }

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("voice_orb_button")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 120.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2.6f) * breathingScale
            val effectiveAmp = if (orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING) {
                amplitude.coerceIn(0.1f, 1f)
            } else {
                0.05f
            }

            // 1. Draw outer glowing halo rings
            val haloRadius = baseRadius + (effectiveAmp * 35.dp.toPx())
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.45f),
                        secondaryColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = haloRadius * 1.35f
                ),
                radius = haloRadius * 1.35f,
                center = center
            )

            // 2. Draw outer rotating dashed orbital rings
            drawOrbitalRing(
                center = center,
                radius = haloRadius * 1.08f,
                rotationAngle = rotationAngle,
                color = primaryColor.copy(alpha = 0.6f),
                strokeWidth = 2.dp.toPx(),
                dashCount = 12
            )

            drawOrbitalRing(
                center = center,
                radius = haloRadius * 0.95f,
                rotationAngle = reverseRotationAngle,
                color = secondaryColor.copy(alpha = 0.5f),
                strokeWidth = 1.5.dp.toPx(),
                dashCount = 8
            )

            // 3. Draw dynamic energy waveform circle
            if (orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING || orbState == OrbState.PROCESSING) {
                drawWaveformPerimeter(
                    center = center,
                    baseRadius = baseRadius * 0.9f,
                    amplitude = effectiveAmp,
                    rotation = rotationAngle,
                    color = primaryColor
                )
            }

            // 4. Central Holographic Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        primaryColor,
                        secondaryColor.copy(alpha = 0.8f),
                        Color(0xFF070B18)
                    ),
                    center = center,
                    radius = baseRadius * 0.75f
                ),
                radius = baseRadius * 0.75f,
                center = center
            )

            // 5. Specular highlight for crystal/orb aesthetic
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.7f), Color.Transparent),
                    center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.25f),
                    radius = baseRadius * 0.35f
                ),
                radius = baseRadius * 0.35f,
                center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.25f)
            )
        }

        // Center Icon Indicator
        val icon = when (orbState) {
            OrbState.DISCONNECTED -> Icons.Default.MicOff
            OrbState.CONNECTING -> Icons.Default.Mic
            OrbState.LISTENING -> Icons.Default.Mic
            OrbState.PROCESSING -> Icons.Default.Psychology
            OrbState.SPEAKING -> Icons.Default.VolumeUp
            OrbState.EXECUTING_ACTION -> Icons.Default.Bolt
        }

        Icon(
            imageVector = icon,
            contentDescription = "Orb state: ${orbState.name}",
            tint = Color.White,
            modifier = Modifier.size(38.dp)
        )
    }
}

private fun DrawScope.drawOrbitalRing(
    center: Offset,
    radius: Float,
    rotationAngle: Float,
    color: Color,
    strokeWidth: Float,
    dashCount: Int
) {
    val step = (2 * Math.PI / dashCount).toFloat()
    val segmentLength = step * 0.6f

    for (i in 0 until dashCount) {
        val startRad = i * step + Math.toRadians(rotationAngle.toDouble()).toFloat()
        val endRad = startRad + segmentLength

        val path = Path().apply {
            val startX = center.x + radius * cos(startRad)
            val startY = center.y + radius * sin(startRad)
            moveTo(startX, startY)

            // Approximate arc with subdivisions
            for (j in 1..4) {
                val subRad = startRad + (segmentLength * (j / 4f))
                val px = center.x + radius * cos(subRad)
                val py = center.y + radius * sin(subRad)
                lineTo(px, py)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

private fun DrawScope.drawWaveformPerimeter(
    center: Offset,
    baseRadius: Float,
    amplitude: Float,
    rotation: Float,
    color: Color
) {
    val points = 36
    val path = Path()
    val rotRad = Math.toRadians(rotation.toDouble()).toFloat()

    for (i in 0..points) {
        val angle = (i.toFloat() / points) * 2 * Math.PI.toFloat() + rotRad
        val waveMod = sin((i * 4).toDouble()).toFloat() * (amplitude * 18.dp.toPx())
        val r = baseRadius + waveMod
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color.copy(alpha = 0.75f),
        style = Stroke(width = 2.dp.toPx())
    )
}
