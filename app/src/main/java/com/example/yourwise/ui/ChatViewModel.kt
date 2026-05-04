package com.example.yourwise.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourwise.data.Message
import com.example.yourwise.network.OpenAIService
import com.example.yourwise.automation.DeviceAutomation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val openAIService = OpenAIService()
    private lateinit var deviceAutomation: DeviceAutomation

    fun sendMessage(userMessage: String, context: Context) {
        if (!::deviceAutomation.isInitialized) {
            deviceAutomation = DeviceAutomation(context)
        }

        // Add user message
        _messages.value = _messages.value + Message(text = userMessage, isUser = true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Get AI response
                val aiResponse = openAIService.getResponse(userMessage)

                // Add AI message
                _messages.value = _messages.value + Message(text = aiResponse, isUser = false)

                // Parse and execute automation commands
                val commands = parseCommands(userMessage.lowercase())
                for (command in commands) {
                    deviceAutomation.executeCommand(command)
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + Message(
                    text = "Error: ${e.message}",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseCommands(message: String): List<String> {
        val commands = mutableListOf<String>()
        when {
            message.contains("wifi") && message.contains("on") -> commands.add("wifi_on")
            message.contains("wifi") && message.contains("off") -> commands.add("wifi_off")
            message.contains("bluetooth") && message.contains("on") -> commands.add("bluetooth_on")
            message.contains("bluetooth") && message.contains("off") -> commands.add("bluetooth_off")
            message.contains("brightness") -> commands.add("brightness_${extractNumber(message)}")
            message.contains("volume") -> commands.add("volume_${extractNumber(message)}")
            message.contains("airplane") && message.contains("on") -> commands.add("airplane_on")
            message.contains("airplane") && message.contains("off") -> commands.add("airplane_off")
        }
        return commands
    }

    private fun extractNumber(text: String): Int {
        val regex = "\\d+".toRegex()
        return regex.find(text)?.value?.toInt() ?: 50
    }
}