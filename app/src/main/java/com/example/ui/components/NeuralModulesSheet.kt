package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CodeFixerResult
import com.example.data.models.NeuralModuleType
import com.example.data.models.ResearchReport
import com.example.data.models.RiskLevel
import com.example.data.models.VaultCategory
import com.example.data.models.VaultItem
import com.example.ui.theme.CyberBackground
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
import com.example.viewmodel.MainAssistantViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuralModulesSheet(
    viewModel: MainAssistantViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedModule by viewModel.selectedModuleSheet.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberBackgroundElevated,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedModule?.title ?: "VASH AI NEURAL MODULES",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sheet",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedModule == null) {
                // Modules Grid Hub
                ModulesGridHub(onSelect = { viewModel.openModuleSheet(it) })
            } else {
                // Specific Module View
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedModule!!) {
                        NeuralModuleType.FILE_VAULT -> FileVaultView(viewModel)
                        NeuralModuleType.ANTIVIRUS -> AntivirusView(viewModel)
                        NeuralModuleType.SYSTEM_CONTROL -> SystemControlsView(viewModel)
                        NeuralModuleType.CPU_MONITOR -> CpuMonitorView(viewModel)
                        NeuralModuleType.ANTI_THEFT -> AntiTheftView(viewModel)
                        NeuralModuleType.CODE_FIXER -> CodeFixerView(viewModel)
                        NeuralModuleType.DEEP_RESEARCH -> DeepResearchView(viewModel)
                        NeuralModuleType.SOUNDSCAPE -> SoundscapeView(viewModel)
                        NeuralModuleType.STOCKS_CRYPTO -> StocksCryptoView(viewModel)
                        NeuralModuleType.TASK_SCHEDULER -> TaskSchedulerView(viewModel)
                        NeuralModuleType.CAMERA_VISION -> {
                            onDismiss()
                            viewModel.openCameraVision()
                        }
                        NeuralModuleType.VOICE_CORE -> {
                            onDismiss()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.closeModuleSheet() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                ) {
                    Text("← Back to Neural Hub", color = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun ModulesGridHub(onSelect: (NeuralModuleType) -> Unit) {
    val modules = NeuralModuleType.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(modules) { module ->
            CyberGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(module) },
                borderColor = CyberBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val icon = getModuleIcon(module)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant)
                            .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = module.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

private fun getModuleIcon(type: NeuralModuleType): ImageVector {
    return when (type) {
        NeuralModuleType.VOICE_CORE -> Icons.Default.Mic
        NeuralModuleType.FILE_VAULT -> Icons.Default.Lock
        NeuralModuleType.ANTIVIRUS -> Icons.Default.Shield
        NeuralModuleType.SYSTEM_CONTROL -> Icons.Default.Tune
        NeuralModuleType.CPU_MONITOR -> Icons.Default.Memory
        NeuralModuleType.CAMERA_VISION -> Icons.Default.Camera
        NeuralModuleType.CODE_FIXER -> Icons.Default.Code
        NeuralModuleType.DEEP_RESEARCH -> Icons.Default.Search
        NeuralModuleType.ANTI_THEFT -> Icons.Default.Security
        NeuralModuleType.SOUNDSCAPE -> Icons.Default.MusicNote
        NeuralModuleType.STOCKS_CRYPTO -> Icons.Default.TrendingUp
        NeuralModuleType.TASK_SCHEDULER -> Icons.Default.Bolt
    }
}

// --- Specific Module Views ---

@Composable
fun FileVaultView(viewModel: MainAssistantViewModel) {
    val items by viewModel.neuralModules.vaultItems.collectAsState()
    var pinInput by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CyberGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonCyan.copy(alpha = 0.4f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AES ENCLAVE VAULT", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("PIN: 1337 or 0000 to unlock", color = TextSecondary, fontSize = 11.sp)
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item", tint = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { item ->
                CyberGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (item.isLocked) CyberBorder else NeonGreen.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.neuralModules.toggleVaultItemLock(item.id, "1337")
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "Lock status",
                                        tint = if (item.isLocked) NeonAmber else NeonGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.neuralModules.deleteVaultItem(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (item.isLocked) {
                            Text("•••••••••••••••• (Encrypted)", color = TextTertiary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        } else {
                            Text(item.secretContent, color = NeonGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AntivirusView(viewModel: MainAssistantViewModel) {
    val results by viewModel.neuralModules.scanResults.collectAsState()
    val isScanning by viewModel.neuralModules.isScanning.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isScanning) {
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("DEEP NEURAL SCANNING RUNTIME...", color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                } else {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SYSTEM INTEGRITY SECURE", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Real-time behavioral heuristic sandbox active", color = TextSecondary, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { scope.launch { viewModel.neuralModules.runAntivirusScan() } },
                    enabled = !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Run Full Threat Scan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("INSPECTION AUDIT LOGS", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { res ->
                CyberGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = when (res.riskLevel) {
                        RiskLevel.CLEAN -> NeonGreen.copy(alpha = 0.4f)
                        RiskLevel.SUSPICIOUS -> NeonAmber.copy(alpha = 0.8f)
                        RiskLevel.CRITICAL -> NeonRed
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(res.fileName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(res.signature, color = TextTertiary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(res.status, color = if (res.riskLevel == RiskLevel.CLEAN) NeonGreen else NeonAmber, fontSize = 11.sp)
                        }

                        if (res.riskLevel != RiskLevel.CLEAN && !res.isQuarantined) {
                            Button(
                                onClick = { viewModel.neuralModules.quarantineThreat(res.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Quarantine", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemControlsView(viewModel: MainAssistantViewModel) {
    val status by viewModel.neuralModules.systemStatus.collectAsState()

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("SCREEN BRIGHTNESS (${status.brightnessPercent}%)", color = NeonCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Slider(
                    value = status.brightnessPercent.toFloat(),
                    onValueChange = { viewModel.neuralModules.setBrightnessLevel(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )
            }
        }

        CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("MEDIA VOLUME (${status.volumePercent}%)", color = NeonViolet, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Slider(
                    value = status.volumePercent.toFloat(),
                    onValueChange = { viewModel.neuralModules.setVolumeLevel(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = NeonViolet, activeTrackColor = NeonViolet)
                )
            }
        }

        CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = if (status.flashlightOn) NeonAmber else TextTertiary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("FLASHLIGHT TORCH", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (status.flashlightOn) "LED Active" else "LED Off", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                Switch(
                    checked = status.flashlightOn,
                    onCheckedChange = { viewModel.neuralModules.toggleFlashlight(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonAmber, checkedTrackColor = NeonAmber.copy(alpha = 0.4f))
                )
            }
        }
    }
}

@Composable
fun CpuMonitorView(viewModel: MainAssistantViewModel) {
    val processes by viewModel.neuralModules.activeProcesses.collectAsState()
    val status by viewModel.neuralModules.systemStatus.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SystemTelemetryStatusBar(status = status, onOpenDiagnostics = {})

        Spacer(modifier = Modifier.height(14.dp))
        Text("ACTIVE SYSTEM PROCESSES", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(processes) { proc ->
                CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(proc.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("PID ${proc.pid} • RAM: ${proc.memoryMb}MB • CPU: ${proc.cpuPercent}%", color = TextTertiary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        if (!proc.isSystem) {
                            Button(
                                onClick = { viewModel.neuralModules.killProcess(proc.pid) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(alpha = 0.8f)),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Kill", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AntiTheftView(viewModel: MainAssistantViewModel) {
    val status by viewModel.neuralModules.systemStatus.collectAsState()
    val triggered by viewModel.neuralModules.antiTheftTriggered.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CyberGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (triggered) NeonRed else if (status.antiTheftArmed) NeonAmber else CyberBorder
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = if (triggered) NeonRed else if (status.antiTheftArmed) NeonAmber else TextTertiary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (triggered) "⚠️ THEFT ALARM TRIGGERED!" else if (status.antiTheftArmed) "MOTION DETECTION ARMED" else "ANTI-THEFT DISARMED",
                    color = if (triggered) NeonRed else if (status.antiTheftArmed) NeonAmber else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Triggers siren & flashlight upon unauthorized movement",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (triggered) {
                    Button(
                        onClick = { viewModel.neuralModules.disarmAlarm() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Text("Disarm Alarm (Enter PIN)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.neuralModules.armAntiTheft(!status.antiTheftArmed) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (status.antiTheftArmed) NeonRed else NeonAmber)
                    ) {
                        Text(
                            if (status.antiTheftArmed) "Disarm Motion Sensor" else "Arm Anti-Theft Protection",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeFixerView(viewModel: MainAssistantViewModel) {
    var inputCode by remember {
        mutableStateOf(
            """
fun filterData(list: List<String?>?): List<String> {
    val res = mutableListOf<String>()
    for (item in list!!) {
        if (item != null) {
            res.add(item.toUpperCase())
        }
    }
    return res
}
            """.trimIndent()
        )
    }
    var language by remember { mutableStateOf("kotlin") }
    val latestResult by viewModel.latestCodeFixer.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            OutlinedTextField(
                value = inputCode,
                onValueChange = { inputCode = it },
                label = { Text("Code Input ($language)", color = NeonCyan) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val res = viewModel.neuralModules.fixCodeSnippet(inputCode, language)
                    viewModel.speakResponse("Code refactored! Check out the optimized logic below.")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
            ) {
                Text("Refactor & Fix with MAX AI", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        latestResult?.let { res ->
            item {
                CyberGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonGreen) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("OPTIMIZED OUTPUT", color = NeonGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(res.fixedCode, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("ANALYSIS", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(res.explanation, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DeepResearchView(viewModel: MainAssistantViewModel) {
    var topic by remember { mutableStateOf("Autonomous Multimodal Agents in 2026") }
    val report by viewModel.latestResearchReport.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Research Topic", color = NeonCyan) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val rep = viewModel.neuralModules.generateResearchReport(topic)
                viewModel.speakResponse("Research complete on $topic! Key consensus loaded on screen.")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
        ) {
            Text("Compile Deep Research Report", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        report?.let { rep ->
            CyberGlassCard(modifier = Modifier.fillMaxWidth().weight(1f), borderColor = NeonCyan) {
                LazyColumn(modifier = Modifier.padding(14.dp)) {
                    item {
                        Text("SYNTHESIZED REPORT: ${rep.topic}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(rep.summary, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("KEY TAKEAWAYS:", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    items(rep.keyTakeaways) { t ->
                        Text("• $t", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SoundscapeView(viewModel: MainAssistantViewModel) {
    val presets = listOf(
        "Neon Dreams" to "Cyber Ambient Synthwave (120 BPM)",
        "Deep Space Lo-Fi" to "Relaxing Interstellar Frequencies",
        "Rain & Holograms" to "Binaural Cyberpunk Rain Ambience",
        "Overclocked Matrix" to "High-Energy Neural Flow Beat"
    )

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        presets.forEach { (name, desc) ->
            CyberGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.voiceEngine.playTone(android.media.ToneGenerator.TONE_PROP_BEEP)
                        viewModel.speakResponse("Playing $name soundscape for your cyber focus session.")
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(desc, color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonPink)
                }
            }
        }
    }
}

@Composable
fun StocksCryptoView(viewModel: MainAssistantViewModel) {
    val items by viewModel.neuralModules.stockItems.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            CyberGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (item.isPositive) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.symbol, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text(item.name, color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("$${item.price}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = "${if (item.isPositive) "+" else ""}${item.changePercent24h}%",
                            color = if (item.isPositive) NeonGreen else NeonRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSchedulerView(viewModel: MainAssistantViewModel) {
    val tasks by viewModel.neuralModules.scheduledTasks.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(tasks) { task ->
            CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(task.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Cron: ${task.cronExpression} • ${task.command}", color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("Last Run: ${task.lastRun}", color = TextTertiary, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (task.isEnabled) NeonGreen else TextTertiary)
                    )
                }
            }
        }
    }
}
