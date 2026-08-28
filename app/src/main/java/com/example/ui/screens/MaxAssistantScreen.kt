package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.NeuralModuleType
import com.example.data.models.OrbState
import com.example.ui.components.CameraVisionDialog
import com.example.ui.components.CyberGlassCard
import com.example.ui.components.GlowingVoiceOrb
import com.example.ui.components.HolographicAvatarBadge
import com.example.ui.components.LiveCaptionBox
import com.example.ui.components.NeuralModulesSheet
import com.example.ui.components.QuickVoicePromptsRow
import com.example.ui.components.SettingsSheet
import com.example.ui.components.SystemTelemetryStatusBar
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBackgroundElevated
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.viewmodel.MainAssistantViewModel

@Composable
fun MaxAssistantScreen(
    viewModel: MainAssistantViewModel = viewModel()
) {
    val context = LocalContext.current

    val orbState by viewModel.orbState.collectAsState()
    val expression by viewModel.avatarExpression.collectAsState()
    val caption by viewModel.currentLiveCaption.collectAsState()
    val activeTool by viewModel.activeToolAction.collectAsState()
    val audioAmp by viewModel.voiceEngine.audioAmplitude.collectAsState()
    val systemStatus by viewModel.neuralModules.systemStatus.collectAsState()

    val selectedModuleSheet by viewModel.selectedModuleSheet.collectAsState()
    val showSettings by viewModel.showSettingsSheet.collectAsState()
    val showCameraVision by viewModel.showCameraVisionDialog.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val micGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (micGranted) {
            viewModel.toggleVoiceOrb()
        }
    }

    fun checkAndStartVoice() {
        val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!hasMic) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
            )
        } else {
            viewModel.toggleVoiceOrb()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = CyberBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonViolet.copy(alpha = 0.08f),
                            NeonCyan.copy(alpha = 0.04f),
                            CyberBackground
                        ),
                        radius = 1200f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top HUD Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HolographicAvatarBadge(
                        expression = expression,
                        orbState = orbState
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { viewModel.openCameraVision() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberBackgroundElevated)
                                .testTag("camera_vision_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Camera Vision",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openModuleSheet(NeuralModuleType.VOICE_CORE) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberBackgroundElevated)
                                .testTag("neural_hub_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Neural Modules Hub",
                                tint = NeonViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openSettings() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberBackgroundElevated)
                                .testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // System Telemetry Bar
                SystemTelemetryStatusBar(
                    status = systemStatus,
                    onOpenDiagnostics = { viewModel.openModuleSheet(NeuralModuleType.CPU_MONITOR) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Center Main Holographic Voice Orb
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GlowingVoiceOrb(
                            orbState = orbState,
                            amplitude = audioAmp,
                            onClick = { checkAndStartVoice() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        WaveformVisualizer(
                            orbState = orbState,
                            amplitude = audioAmp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }

                // Live Captions Box
                LiveCaptionBox(
                    caption = caption,
                    orbState = orbState,
                    activeTool = activeTool,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Prompts Row
                QuickVoicePromptsRow(
                    onPromptClick = { promptText ->
                        viewModel.handleUserVoiceInput(promptText)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Lock,
                        label = "Vault",
                        accentColor = NeonCyan,
                        onClick = { viewModel.openModuleSheet(NeuralModuleType.FILE_VAULT) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Shield,
                        label = "Threat Scan",
                        accentColor = NeonGreen,
                        onClick = { viewModel.openModuleSheet(NeuralModuleType.ANTIVIRUS) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Security,
                        label = "Anti-Theft",
                        accentColor = NeonPink,
                        onClick = { viewModel.openModuleSheet(NeuralModuleType.ANTI_THEFT) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.GridView,
                        label = "All Modules",
                        accentColor = NeonViolet,
                        onClick = { viewModel.openModuleSheet(NeuralModuleType.VOICE_CORE) }
                    )
                }
            }

            // Dialogs & Sheets
            if (selectedModuleSheet != null) {
                NeuralModulesSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeModuleSheet() }
                )
            }

            if (showCameraVision) {
                CameraVisionDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeCameraVision() }
                )
            }

            if (showSettings) {
                SettingsSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeSettings() }
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    CyberGlassCard(
        modifier = Modifier
            .clip(CircleShape)
            .padding(2.dp),
        borderColor = accentColor.copy(alpha = 0.4f),
        shape = CircleShape
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
