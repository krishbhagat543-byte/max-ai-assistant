package com.example.data.models

data class VaultItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val category: VaultCategory,
    val secretContent: String,
    val isLocked: Boolean = true,
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class VaultCategory {
    PASSWORD,
    API_KEY,
    CRYPTO_SEED,
    SECURE_NOTE,
    DOCUMENT
}

data class AntivirusScanResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fileName: String,
    val path: String,
    val riskLevel: RiskLevel,
    val signature: String,
    val status: String,
    val isQuarantined: Boolean = false
)

enum class RiskLevel {
    CLEAN,
    SUSPICIOUS,
    CRITICAL
}

data class ScheduledTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val cronExpression: String,
    val command: String,
    val isEnabled: Boolean = true,
    val lastRun: String = "Pending"
)

data class ProcessInfo(
    val pid: Int,
    val name: String,
    val memoryMb: Int,
    val cpuPercent: Float,
    val isSystem: Boolean = false
)

data class SystemStatus(
    val cpuLoadPercent: Int = 18,
    val ramUsedMb: Int = 3420,
    val ramTotalMb: Int = 8192,
    val batteryPercent: Int = 88,
    val isCharging: Boolean = false,
    val wifiSsid: String = "Vash_CyberNet_5G",
    val ipAddress: String = "192.168.1.142",
    val latencyMs: Int = 14,
    val brightnessPercent: Int = 75,
    val volumePercent: Int = 60,
    val flashlightOn: Boolean = false,
    val antiTheftArmed: Boolean = false
)

data class StockCryptoItem(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercent24h: Double,
    val isPositive: Boolean
)

data class ResearchReport(
    val topic: String,
    val summary: String,
    val keyTakeaways: List<String>,
    val sources: List<String>,
    val generatedAt: Long = System.currentTimeMillis()
)

data class CodeFixerResult(
    val language: String,
    val originalCode: String,
    val fixedCode: String,
    val explanation: String,
    val optimizations: List<String>
)

enum class NeuralModuleType(
    val title: String,
    val subtitle: String,
    val category: String,
    val iconName: String
) {
    VOICE_CORE("Live Voice Orb", "Bidirectional Neural Speech Stream", "Core", "Mic"),
    FILE_VAULT("Secure Vault", "AES Enclave & Biometric Lock", "Security", "Lock"),
    ANTIVIRUS("Threat Scanner", "Real-Time Heuristic Malware Inspector", "Security", "Shield"),
    SYSTEM_CONTROL("System Controls", "Brightness, Volume & Power HUD", "System", "Sliders"),
    CPU_MONITOR("Process & CPU", "RAM, Thermal & Thread Monitor", "System", "Cpu"),
    CAMERA_VISION("Camera Vision", "Multimodal Screen & Object OCR", "Vision", "Camera"),
    CODE_FIXER("AI Code Fixer", "Multi-Language Refactor & Bugfix", "Dev", "Code"),
    DEEP_RESEARCH("Research Agent", "Autonomous Web Synthesis Engine", "Agent", "Search"),
    ANTI_THEFT("Anti-Theft Motion", "Accelerometer Alarm & Siren", "Mobile", "AlertTriangle"),
    SOUNDSCAPE("Sound Synthesizer", "Cyberpunk Ambient & Spotify Audio", "Media", "Music"),
    STOCKS_CRYPTO("Markets & Forex", "Live Ticker & Trend Intelligence", "Intelligence", "TrendingUp"),
    TASK_SCHEDULER("Task Scheduler", "Cron Automation & Routine Triggers", "Agent", "Clock")
}
