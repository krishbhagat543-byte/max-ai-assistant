package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.AvatarExpression
import com.example.data.models.OrbState
import com.example.data.models.SystemStatus
import com.example.ui.theme.CyberBackgroundElevated
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderViolet
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CyberGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberBorder,
    backgroundColor: Color = CyberSurface.copy(alpha = 0.85f),
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .border(BorderStroke(1.dp, borderColor), shape),
        color = backgroundColor,
        shape = shape
    ) {
        content()
    }
}

@Composable
fun HolographicAvatarBadge(
    expression: AvatarExpression,
    orbState: OrbState,
    modifier: Modifier = Modifier
) {
    val moodGlowColor = when (expression) {
        AvatarExpression.WITTY -> NeonCyan
        AvatarExpression.FLIRTY -> NeonPink
        AvatarExpression.FOCUSED -> NeonViolet
        AvatarExpression.SMUG -> NeonAmber
        AvatarExpression.SURPRISED -> NeonCyan
        AvatarExpression.TACTICAL -> NeonRed
        AvatarExpression.HAPPY -> NeonGreen
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(CyberSurface.copy(alpha = 0.9f))
            .border(BorderStroke(1.dp, moodGlowColor.copy(alpha = 0.5f)), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.5.dp, moodGlowColor), CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.max_ai_avatar_1787908113692),
                contentDescription = "Max AI Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MAX AI",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (orbState != OrbState.DISCONNECTED) NeonGreen else TextTertiary)
                )
            }
            Text(
                text = expression.displayName,
                color = moodGlowColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SystemTelemetryStatusBar(
    status: SystemStatus,
    onOpenDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberBackgroundElevated.copy(alpha = 0.8f))
            .border(BorderStroke(0.8.dp, CyberBorder), RoundedCornerShape(12.dp))
            .clickable { onOpenDiagnostics() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TelemetryPill(
            icon = Icons.Default.Memory,
            label = "${status.cpuLoadPercent}%",
            accentColor = NeonCyan
        )
        TelemetryPill(
            icon = Icons.Default.Memory,
            label = "${status.ramUsedMb}MB",
            accentColor = NeonViolet
        )
        TelemetryPill(
            icon = Icons.Default.BatteryChargingFull,
            label = "${status.batteryPercent}%",
            accentColor = NeonGreen
        )
        TelemetryPill(
            icon = Icons.Default.Wifi,
            label = "${status.latencyMs}ms",
            accentColor = NeonAmber
        )
        if (status.antiTheftArmed) {
            TelemetryPill(
                icon = Icons.Default.Security,
                label = "ARMED",
                accentColor = NeonRed
            )
        }
    }
}

@Composable
private fun TelemetryPill(
    icon: ImageVector,
    label: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LiveCaptionBox(
    caption: String,
    orbState: OrbState,
    activeTool: String?,
    modifier: Modifier = Modifier
) {
    val borderColor = when (orbState) {
        OrbState.SPEAKING -> NeonPink
        OrbState.LISTENING -> NeonCyan
        OrbState.EXECUTING_ACTION -> NeonAmber
        OrbState.PROCESSING -> NeonViolet
        else -> CyberBorder
    }

    CyberGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_caption_box"),
        borderColor = borderColor.copy(alpha = 0.6f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(borderColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (orbState) {
                            OrbState.LISTENING -> "LISTENING (MIC ACTIVE)"
                            OrbState.PROCESSING -> "NEURAL QUANTUM STREAM..."
                            OrbState.SPEAKING -> "MAX AI SPEAKING"
                            OrbState.EXECUTING_ACTION -> "EXECUTING TOOL CALL"
                            OrbState.CONNECTING -> "ESTABLISHING WEBSOCKET..."
                            OrbState.DISCONNECTED -> "STANDBY // TAP TO SPEAK"
                        },
                        color = borderColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                if (activeTool != null) {
                    Text(
                        text = "[$activeTool]",
                        color = NeonAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = caption,
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickVoicePromptsRow(
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        "Scan system for malware" to Icons.Default.Shield,
        "Arm anti-theft motion alarm" to Icons.Default.Security,
        "Fix my Kotlin code" to Icons.Default.Bolt,
        "Deep research AI trends" to Icons.Default.Search,
        "Toggle flashlight torch" to Icons.Default.FlashOn,
        "What do you see in camera?" to Icons.Default.Memory,
        "Check crypto & stock prices" to Icons.Default.Bolt
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { (text, icon) ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.9f))
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(20.dp))
                    .clickable { onPromptClick(text) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
