package com.example.data.gemini

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.models.GeminiContent
import com.example.data.models.GeminiFunctionCall
import com.example.data.models.GeminiFunctionResponse
import com.example.data.models.GeminiGenConfig
import com.example.data.models.GeminiGenerateRequest
import com.example.data.models.GeminiGenerateResponse
import com.example.data.models.GeminiInlineData
import com.example.data.models.GeminiPart
import com.example.personality.MaxPersona
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): GeminiGenerateResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    fun getApiKey(userCustomKey: String?): String {
        if (!userCustomKey.isNullOrBlank()) {
            return userCustomKey.trim()
        }
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }
}

class GeminiRepository {

    suspend fun sendVoiceConversation(
        userMessage: String,
        conversationHistory: List<GeminiContent>,
        userCustomApiKey: String? = null
    ): GeminiResponseResult = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey(userCustomApiKey)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiResponseResult.Success(
                spokenText = "Hey there! Please set your Gemini API key in Settings so I can talk back using live neural generation, but my local Vash AI neural modules are 100% active!",
                functionCall = null
            )
        }

        val updatedContents = conversationHistory.toMutableList().apply {
            add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = userMessage))
                )
            )
        }

        val request = GeminiGenerateRequest(
            contents = updatedContents,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = MaxPersona.SYSTEM_PROMPT))
            ),
            generationConfig = GeminiGenConfig(
                temperature = 0.85f,
                topP = 0.95f,
                maxOutputTokens = 800
            ),
            tools = MaxPersona.getToolDefinitions()
        )

        try {
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            val firstCandidate = response.candidates?.firstOrNull()
            val candidateContent = firstCandidate?.content
            val firstPart = candidateContent?.parts?.firstOrNull()

            if (firstPart?.functionCall != null) {
                return@withContext GeminiResponseResult.Success(
                    spokenText = "Executing ${firstPart.functionCall.name.replace("_", " ")} for you!",
                    functionCall = firstPart.functionCall,
                    rawContent = candidateContent
                )
            }

            val text = firstPart?.text ?: "I heard you, but I'm keeping you in suspense!"
            GeminiResponseResult.Success(
                spokenText = text,
                functionCall = null,
                rawContent = candidateContent
            )
        } catch (e: Exception) {
            Log.e("GeminiRepo", "Error calling Gemini", e)
            GeminiResponseResult.Error(
                message = e.localizedMessage ?: "Failed to connect to MAX neural stream",
                fallbackText = "Oops, looks like my neural link glitched for a second. Try asking me again!"
            )
        }
    }

    suspend fun analyzeCameraImage(
        bitmap: Bitmap,
        userPrompt: String,
        userCustomApiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey(userCustomApiKey)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Camera vision active! (Configure your Gemini API key in Settings to get real-time vision commentary from MAX AI)."
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val base64Data = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        val prompt = if (userPrompt.isNotBlank()) userPrompt else "Describe what you see in front of you with sassy witty personality and point out interesting details."

        val request = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(
                        GeminiPart(text = prompt),
                        GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = MaxPersona.SYSTEM_PROMPT))
            )
        )

        try {
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I see everything, but you didn't give me much to work with!"
        } catch (e: Exception) {
            Log.e("GeminiRepo", "Vision error", e)
            "Vision scan error: ${e.localizedMessage ?: "Could not process image"}"
        }
    }

    suspend fun sendToolResult(
        toolName: String,
        resultMap: Map<String, Any?>,
        conversationHistory: List<GeminiContent>,
        userCustomApiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey(userCustomApiKey)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Action '$toolName' completed successfully!"
        }

        val updatedContents = conversationHistory.toMutableList().apply {
            add(
                GeminiContent(
                    role = "function",
                    parts = listOf(
                        GeminiPart(
                            functionResponse = GeminiFunctionResponse(
                                name = toolName,
                                response = resultMap
                            )
                        )
                    )
                )
            )
        }

        val request = GeminiGenerateRequest(
            contents = updatedContents,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = MaxPersona.SYSTEM_PROMPT + "\nBriefly acknowledge the tool outcome with sass."))
            )
        )

        try {
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Action executed without a hitch."
        } catch (e: Exception) {
            "All done! Action '$toolName' finished."
        }
    }
}

sealed class GeminiResponseResult {
    data class Success(
        val spokenText: String,
        val functionCall: GeminiFunctionCall? = null,
        val rawContent: GeminiContent? = null
    ) : GeminiResponseResult()

    data class Error(
        val message: String,
        val fallbackText: String
    ) : GeminiResponseResult()
}
