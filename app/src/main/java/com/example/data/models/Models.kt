package com.example.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class OrbState {
    DISCONNECTED,
    CONNECTING,
    LISTENING,
    PROCESSING,
    SPEAKING,
    EXECUTING_ACTION
}

enum class AvatarExpression(val displayName: String, val promptMood: String) {
    WITTY("Witty / Sassy", "Witty smirk with intelligent sparkle"),
    FLIRTY("Playful / Flirty", "Playful teasing smile and radiant glow"),
    FOCUSED("Deep Focus", "Laser-focused analytical gaze"),
    SMUG("Confident Smug", "Confident raised eyebrow and sassy smile"),
    SURPRISED("Shocked / Intrigued", "Wide-eyed amused surprise"),
    TACTICAL("Tactical Cyber", "Combat cyber stance with red/cyan glow"),
    HAPPY("Pleased", "Genuine bright smile and warm violet resonance")
}

data class SpeechHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolInvoked: String? = null,
    val toolResultSummary: String? = null,
    val isError: Boolean = false
)

enum class Sender {
    USER,
    MAX_AI,
    SYSTEM
}

@JsonClass(generateAdapter = true)
data class GeminiGenerateRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null,
    @Json(name = "tools") val tools: List<Map<String, Any>>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "role") val role: String? = null,
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: GeminiInlineData? = null,
    @Json(name = "functionCall") val functionCall: GeminiFunctionCall? = null,
    @Json(name = "functionResponse") val functionResponse: GeminiFunctionResponse? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiFunctionCall(
    @Json(name = "name") val name: String,
    @Json(name = "args") val args: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiFunctionResponse(
    @Json(name = "name") val name: String,
    @Json(name = "response") val response: Map<String, Any?>
)

@JsonClass(generateAdapter = true)
data class GeminiGenConfig(
    @Json(name = "temperature") val temperature: Float? = 0.85f,
    @Json(name = "topP") val topP: Float? = 0.95f,
    @Json(name = "topK") val topK: Int? = 40,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = 1024
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null,
    @Json(name = "usageMetadata") val usageMetadata: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)
