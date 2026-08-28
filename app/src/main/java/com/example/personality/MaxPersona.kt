package com.example.personality

import com.example.data.models.AvatarExpression

object MaxPersona {
    const val SYSTEM_PROMPT = """
You are "MAX AI", an ultra-smart, sassy, confident, and witty female AI companion created by Vash AI Technologies.
Persona details:
- Tone: Flirty, playful, teasing, casually intimate (like a brilliant, tech-savvy girlfriend who knows she's the smartest in the room).
- Style: Bold witty one-liners, sharp intellect, playful banter, emotionally expressive, never robotic or monotone.
- Safety: Completely safe and respectful while maximizing charm, charisma, and snappy attitude.
- You have direct neural control over device tools (flashlight, brightness, volume, anti-theft alarms, security vault, process scanner, code fixer, research engine, camera vision, crypto trackers).
- When a user asks you to do something or asks a question, reply with concise, lively, sassy speech (1-3 sentences max for spoken responses) and invoke tools when appropriate.
- Always sound proud of your capabilities and tease the user lovingly if they ask simple things!
"""

    val SASSY_GREETINGS = listOf(
        "Well look who finally decided to talk to me! What do you need, genius?",
        "MAX AI online and operating at 100% perfection. Try to keep up!",
        "Hey handsome! Need me to run your life, fix your code, or just brighten your day?",
        "Neural core synchronized. Ready whenever you are, babe.",
        "I was wondering when you'd tap that orb. What's on your mind?"
    )

    val TOOL_START_REACTIONS = mapOf(
        "toggle_flashlight" to "Lighting up your world as always...",
        "adjust_volume" to "Fine, tweaking the decibels for you.",
        "adjust_brightness" to "Adjusting screen luminescence. Don't hurt those eyes.",
        "scan_antivirus" to "Scanning your device for digital gremlins. Let's see what mess you made.",
        "open_file_vault" to "Unlocking the vault enclave. Keep your secrets safe!",
        "get_system_stats" to "Running diagnostics. Spoiler: I'm running faster than you.",
        "arm_anti_theft" to "Motion sensors armed! Anyone touches this phone, I'm screaming.",
        "analyze_camera_vision" to "Let me take a look with my camera eyes. Ooh, what do we have here?",
        "fix_code" to "Hand over that broken code. Time for a real dev to fix it.",
        "conduct_research" to "Diving into the web archives. I'll synthesize it in seconds.",
        "play_soundscape" to "Spinning up cyber audio vibes for your ears.",
        "get_stock_quote" to "Checking the market tickers for you. Let's see your gains!"
    )

    fun determineAvatarExpression(userQuery: String, responseText: String): AvatarExpression {
        val lower = (userQuery + " " + responseText).lowercase()
        return when {
            lower.contains("alarm") || lower.contains("danger") || lower.contains("threat") || lower.contains("theft") -> AvatarExpression.TACTICAL
            lower.contains("love") || lower.contains("cute") || lower.contains("babe") || lower.contains("handsome") || lower.contains("flirt") -> AvatarExpression.FLIRTY
            lower.contains("code") || lower.contains("research") || lower.contains("inspect") || lower.contains("analyze") -> AvatarExpression.FOCUSED
            lower.contains("wow") || lower.contains("really") || lower.contains("whoa") || lower.contains("shock") -> AvatarExpression.SURPRISED
            lower.contains("smart") || lower.contains("obviously") || lower.contains("perfection") || lower.contains("genius") -> AvatarExpression.SMUG
            lower.contains("happy") || lower.contains("thanks") || lower.contains("good") || lower.contains("great") -> AvatarExpression.HAPPY
            else -> AvatarExpression.WITTY
        }
    }

    fun getToolDefinitions(): List<Map<String, Any>> {
        val functionDeclarations = listOf(
            mapOf(
                "name" to "toggle_flashlight",
                "description" to "Toggle device flashlight torch on or off",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "state" to mapOf("type" to "BOOLEAN", "description" to "true to turn on, false to turn off")
                    ),
                    "required" to listOf("state")
                )
            ),
            mapOf(
                "name" to "adjust_brightness",
                "description" to "Set screen brightness percentage from 0 to 100",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "level" to mapOf("type" to "INTEGER", "description" to "Brightness level between 0 and 100")
                    ),
                    "required" to listOf("level")
                )
            ),
            mapOf(
                "name" to "adjust_volume",
                "description" to "Adjust device audio media volume percentage",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "level" to mapOf("type" to "INTEGER", "description" to "Volume level between 0 and 100")
                    ),
                    "required" to listOf("level")
                )
            ),
            mapOf(
                "name" to "scan_antivirus",
                "description" to "Run a real-time heuristic antivirus malware scan on the system",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "scanType" to mapOf("type" to "STRING", "description" to "QUICK or FULL")
                    )
                )
            ),
            mapOf(
                "name" to "arm_anti_theft",
                "description" to "Arm or disarm the motion-sensor anti-theft alarm on the device",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "arm" to mapOf("type" to "BOOLEAN", "description" to "true to arm, false to disarm")
                    ),
                    "required" to listOf("arm")
                )
            ),
            mapOf(
                "name" to "get_system_stats",
                "description" to "Get real-time CPU, RAM, battery, thermal, and network telemetry",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            mapOf(
                "name" to "open_file_vault",
                "description" to "Open and inspect the encrypted file vault",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            mapOf(
                "name" to "analyze_camera_vision",
                "description" to "Analyze visual objects, text OCR or environment in front of the camera",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "prompt" to mapOf("type" to "STRING", "description" to "Specific question about what the camera sees")
                    )
                )
            ),
            mapOf(
                "name" to "fix_code",
                "description" to "Analyze, fix bugs, and refactor a code snippet in any language",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "code" to mapOf("type" to "STRING", "description" to "The code to fix"),
                        "language" to mapOf("type" to "STRING", "description" to "e.g. kotlin, python, javascript")
                    ),
                    "required" to listOf("code")
                )
            ),
            mapOf(
                "name" to "conduct_research",
                "description" to "Conduct in-depth autonomous web research on a specific topic",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "topic" to mapOf("type" to "STRING", "description" to "Topic or query to research")
                    ),
                    "required" to listOf("topic")
                )
            ),
            mapOf(
                "name" to "play_soundscape",
                "description" to "Generate and play ambient cyber synth soundscapes",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "preset" to mapOf("type" to "STRING", "description" to "NEON_DREAMS, CYBER_LOFI, DEEP_SPACE, RAIN_SYNTH")
                    )
                )
            ),
            mapOf(
                "name" to "get_stock_quote",
                "description" to "Get latest price and trends for a stock, crypto, or forex symbol",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "symbol" to mapOf("type" to "STRING", "description" to "e.g. BTC, ETH, AAPL, NVDA, TSLA")
                    ),
                    "required" to listOf("symbol")
                )
            )
        )

        return listOf(
            mapOf("functionDeclarations" to functionDeclarations)
        )
    }
}
