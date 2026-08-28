package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiRepository
import com.example.data.gemini.GeminiResponseResult
import com.example.data.models.AvatarExpression
import com.example.data.models.CodeFixerResult
import com.example.data.models.GeminiContent
import com.example.data.models.GeminiFunctionCall
import com.example.data.models.GeminiPart
import com.example.data.models.NeuralModuleType
import com.example.data.models.OrbState
import com.example.data.models.ResearchReport
import com.example.data.models.Sender
import com.example.data.models.SpeechHistoryItem
import com.example.modules.NeuralModulesManager
import com.example.personality.MaxPersona
import com.example.voice.VoiceEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainAssistantViewModel(application: Application) : AndroidViewModel(application) {

    val voiceEngine = VoiceEngine(application.applicationContext)
    val neuralModules = NeuralModulesManager(application.applicationContext)
    private val geminiRepository = GeminiRepository()

    // --- Core Voice & Orb State ---
    private val _orbState = MutableStateFlow(OrbState.DISCONNECTED)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    private val _avatarExpression = MutableStateFlow(AvatarExpression.WITTY)
    val avatarExpression: StateFlow<AvatarExpression> = _avatarExpression.asStateFlow()

    private val _speechHistory = MutableStateFlow<List<SpeechHistoryItem>>(
        listOf(
            SpeechHistoryItem(
                sender = Sender.MAX_AI,
                text = "MAX AI online and fully synchronized. Tap the neural orb to start talking, or open any Vash AI module!"
            )
        )
    )
    val speechHistory: StateFlow<List<SpeechHistoryItem>> = _speechHistory.asStateFlow()

    private val _currentLiveCaption = MutableStateFlow("Tap orb to start voice interaction")
    val currentLiveCaption: StateFlow<String> = _currentLiveCaption.asStateFlow()

    private val _activeToolAction = MutableStateFlow<String?>(null)
    val activeToolAction: StateFlow<String?> = _activeToolAction.asStateFlow()

    // --- Active Sub-Sheets & Dialogs ---
    private val _selectedModuleSheet = MutableStateFlow<NeuralModuleType?>(null)
    val selectedModuleSheet: StateFlow<NeuralModuleType?> = _selectedModuleSheet.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private val _showCameraVisionDialog = MutableStateFlow(false)
    val showCameraVisionDialog: StateFlow<Boolean> = _showCameraVisionDialog.asStateFlow()

    // --- Settings / Preferences ---
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _voicePitch = MutableStateFlow(1.15f)
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceRate = MutableStateFlow(1.05f)
    val voiceRate: StateFlow<Float> = _voiceRate.asStateFlow()

    private val _sassLevel = MutableStateFlow("Sassy") // Gentle, Sassy, Savage
    val sassLevel: StateFlow<String> = _sassLevel.asStateFlow()

    // --- Specific Module Results ---
    private val _latestResearchReport = MutableStateFlow<ResearchReport?>(null)
    val latestResearchReport: StateFlow<ResearchReport?> = _latestResearchReport.asStateFlow()

    private val _latestCodeFixer = MutableStateFlow<CodeFixerResult?>(null)
    val latestCodeFixer: StateFlow<CodeFixerResult?> = _latestCodeFixer.asStateFlow()

    private val conversationHistory = mutableListOf<GeminiContent>()
    private var activeProcessingJob: Job? = null

    init {
        // Wire voice engine callbacks
        voiceEngine.onSpeechRecognized = { transcript ->
            handleUserVoiceInput(transcript)
        }

        voiceEngine.onSpeechError = { errorMsg ->
            if (_orbState.value == OrbState.LISTENING) {
                _orbState.value = OrbState.DISCONNECTED
                _currentLiveCaption.value = errorMsg
            }
        }

        voiceEngine.onSpeakingFinished = {
            if (_orbState.value == OrbState.SPEAKING) {
                _orbState.value = OrbState.DISCONNECTED
                _currentLiveCaption.value = "Listening paused. Tap orb to chat."
            }
        }

        // Forward live speech partials to caption
        viewModelScope.launch {
            voiceEngine.partialTranscript.collect { partial ->
                if (_orbState.value == OrbState.LISTENING && partial.isNotBlank()) {
                    _currentLiveCaption.value = partial
                }
            }
        }

        // Listen to anti-theft triggered event
        viewModelScope.launch {
            neuralModules.antiTheftTriggered.collect { triggered ->
                if (triggered) {
                    _avatarExpression.value = AvatarExpression.TACTICAL
                    _orbState.value = OrbState.EXECUTING_ACTION
                    _currentLiveCaption.value = "⚠️ MOTION ALARM TRIGGERED! INTRUSION DETECTED!"
                    voiceEngine.playSirenAlarm()
                    speakResponse("Warning! Motion detected on secured device! Please enter PIN to disarm.")
                }
            }
        }
    }

    fun toggleVoiceOrb() {
        when (_orbState.value) {
            OrbState.DISCONNECTED -> {
                startListening()
            }
            OrbState.LISTENING -> {
                voiceEngine.stopListening()
                _orbState.value = OrbState.DISCONNECTED
                _currentLiveCaption.value = "Voice session paused."
            }
            OrbState.SPEAKING -> {
                voiceEngine.stopSpeaking()
                _orbState.value = OrbState.DISCONNECTED
                _currentLiveCaption.value = "Interrupted. Tap orb to speak."
            }
            OrbState.PROCESSING, OrbState.CONNECTING, OrbState.EXECUTING_ACTION -> {
                activeProcessingJob?.cancel()
                voiceEngine.stopSpeaking()
                voiceEngine.stopListening()
                _orbState.value = OrbState.DISCONNECTED
                _currentLiveCaption.value = "Action cancelled."
            }
        }
    }

    private fun startListening() {
        _orbState.value = OrbState.LISTENING
        _currentLiveCaption.value = "Listening to you..."
        voiceEngine.startListening()
    }

    fun handleUserVoiceInput(userText: String) {
        if (userText.isBlank()) return

        // Add user speech to history
        val userItem = SpeechHistoryItem(sender = Sender.USER, text = userText)
        _speechHistory.value = _speechHistory.value + userItem
        _currentLiveCaption.value = userText

        _orbState.value = OrbState.PROCESSING

        activeProcessingJob?.cancel()
        activeProcessingJob = viewModelScope.launch {
            val result = geminiRepository.sendVoiceConversation(
                userMessage = userText,
                conversationHistory = conversationHistory,
                userCustomApiKey = _customApiKey.value
            )

            when (result) {
                is GeminiResponseResult.Success -> {
                    result.rawContent?.let { conversationHistory.add(it) }

                    if (result.functionCall != null) {
                        executeFunctionCall(result.functionCall, userText)
                    } else {
                        val mood = MaxPersona.determineAvatarExpression(userText, result.spokenText)
                        _avatarExpression.value = mood
                        speakResponse(result.spokenText)
                    }
                }
                is GeminiResponseResult.Error -> {
                    _avatarExpression.value = AvatarExpression.SMUG
                    speakResponse(result.fallbackText, isError = true)
                }
            }
        }
    }

    private suspend fun executeFunctionCall(functionCall: GeminiFunctionCall, originalQuery: String) {
        val toolName = functionCall.name
        _orbState.value = OrbState.EXECUTING_ACTION
        _activeToolAction.value = toolName
        val introReaction = MaxPersona.TOOL_START_REACTIONS[toolName] ?: "Engaging neural module: $toolName..."
        _currentLiveCaption.value = introReaction

        val resultMap = mutableMapOf<String, Any?>()

        when (toolName) {
            "toggle_flashlight" -> {
                val state = functionCall.args?.get("state") as? Boolean ?: true
                val success = neuralModules.toggleFlashlight(state)
                resultMap["success"] = success
                resultMap["flashlightOn"] = state
            }
            "adjust_volume" -> {
                val level = (functionCall.args?.get("level") as? Number)?.toInt() ?: 70
                val newLevel = neuralModules.setVolumeLevel(level)
                resultMap["volumePercent"] = newLevel
            }
            "adjust_brightness" -> {
                val level = (functionCall.args?.get("level") as? Number)?.toInt() ?: 80
                val newLevel = neuralModules.setBrightnessLevel(level)
                resultMap["brightnessPercent"] = newLevel
            }
            "scan_antivirus" -> {
                val results = neuralModules.runAntivirusScan()
                resultMap["scannedItems"] = results.size
                resultMap["threatsFound"] = results.count { it.riskLevel != com.example.data.models.RiskLevel.CLEAN }
                openModuleSheet(NeuralModuleType.ANTIVIRUS)
            }
            "arm_anti_theft" -> {
                val arm = functionCall.args?.get("arm") as? Boolean ?: true
                neuralModules.armAntiTheft(arm)
                resultMap["antiTheftArmed"] = arm
                openModuleSheet(NeuralModuleType.ANTI_THEFT)
            }
            "open_file_vault" -> {
                openModuleSheet(NeuralModuleType.FILE_VAULT)
                resultMap["vaultStatus"] = "Opened"
            }
            "get_system_stats" -> {
                neuralModules.refreshSystemMetrics()
                openModuleSheet(NeuralModuleType.CPU_MONITOR)
                resultMap["status"] = neuralModules.systemStatus.value
            }
            "fix_code" -> {
                val code = functionCall.args?.get("code") as? String ?: "fun test() { println(null) }"
                val lang = functionCall.args?.get("language") as? String ?: "kotlin"
                val res = neuralModules.fixCodeSnippet(code, lang)
                _latestCodeFixer.value = res
                openModuleSheet(NeuralModuleType.CODE_FIXER)
                resultMap["optimizations"] = res.optimizations
            }
            "conduct_research" -> {
                val topic = functionCall.args?.get("topic") as? String ?: "Quantum Machine Learning"
                val report = neuralModules.generateResearchReport(topic)
                _latestResearchReport.value = report
                openModuleSheet(NeuralModuleType.DEEP_RESEARCH)
                resultMap["summary"] = report.summary
            }
            "play_soundscape" -> {
                openModuleSheet(NeuralModuleType.SOUNDSCAPE)
                resultMap["soundscape"] = "Active"
            }
            "get_stock_quote" -> {
                openModuleSheet(NeuralModuleType.STOCKS_CRYPTO)
                resultMap["tickers"] = "Updated"
            }
            "analyze_camera_vision" -> {
                _showCameraVisionDialog.value = true
                resultMap["camera"] = "Opened for live OCR"
            }
            else -> {
                resultMap["status"] = "Executed"
            }
        }

        delay(400)
        val finalResponse = geminiRepository.sendToolResult(
            toolName = toolName,
            resultMap = resultMap,
            conversationHistory = conversationHistory,
            userCustomApiKey = _customApiKey.value
        )

        _activeToolAction.value = null
        val mood = MaxPersona.determineAvatarExpression(originalQuery, finalResponse)
        _avatarExpression.value = mood
        speakResponse(finalResponse, toolInvoked = toolName, toolSummary = "Executed $toolName")
    }

    fun speakResponse(text: String, toolInvoked: String? = null, toolSummary: String? = null, isError: Boolean = false) {
        _speechHistory.value = _speechHistory.value + SpeechHistoryItem(
            sender = Sender.MAX_AI,
            text = text,
            toolInvoked = toolInvoked,
            toolResultSummary = toolSummary,
            isError = isError
        )
        _currentLiveCaption.value = text
        _orbState.value = OrbState.SPEAKING
        voiceEngine.speak(text, pitch = _voicePitch.value, rate = _voiceRate.value)
    }

    fun analyzeCameraFrame(bitmap: Bitmap, prompt: String) {
        viewModelScope.launch {
            _orbState.value = OrbState.PROCESSING
            _currentLiveCaption.value = "Analyzing visual data..."
            val result = geminiRepository.analyzeCameraImage(bitmap, prompt, _customApiKey.value)
            _avatarExpression.value = AvatarExpression.FOCUSED
            speakResponse(result)
        }
    }

    fun openModuleSheet(type: NeuralModuleType) {
        _selectedModuleSheet.value = type
    }

    fun closeModuleSheet() {
        _selectedModuleSheet.value = null
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key.trim()
    }

    fun setVoiceSettings(pitch: Float, rate: Float, sass: String) {
        _voicePitch.value = pitch
        _voiceRate.value = rate
        _sassLevel.value = sass
    }

    fun openSettings() {
        _showSettingsSheet.value = true
    }

    fun closeSettings() {
        _showSettingsSheet.value = false
    }

    fun openCameraVision() {
        _showCameraVisionDialog.value = true
    }

    fun closeCameraVision() {
        _showCameraVisionDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
