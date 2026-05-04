package com.example.yourwise.network

import com.example.yourwise.data.ChatRequest
import com.example.yourwise.data.ChatResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.encodeToString

class OpenAIService {
    private val apiKey = "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" // Replace with your API key
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getResponse(userMessage: String): String {
        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                mapOf(
                    "role" to "system",
                    "content" to "You are a helpful AI assistant that controls Android device settings. Help users automate their device tasks."
                ),
                mapOf(
                    "role" to "user",
                    "content" to userMessage
                )
            ),
            temperature = 0.7,
            max_tokens = 150
        )

        val requestBody = json.encodeToString(request).toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(httpRequest).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return "No response"
                val chatResponse = json.decodeFromString<ChatResponse>(body)
                chatResponse.choices.firstOrNull()?.message?.content ?: "No response"
            } else {
                "API Error: ${response.code}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
