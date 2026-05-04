package com.example.yourwise.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 150
)