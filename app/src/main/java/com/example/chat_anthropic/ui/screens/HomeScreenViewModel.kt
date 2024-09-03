package com.example.chat_anthropic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_anthropic.model.AnthropicRequest
import com.example.chat_anthropic.model.AnthropicResponse
import com.example.chat_anthropic.model.Message
import com.example.chat_anthropic.network.AnthropicApi
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface AnthropicUiState {
    data class Success(val respuesta: List<ChatMessage>) : AnthropicUiState
    data class Error(val message: String) : AnthropicUiState
    //object Loading : AnthropicUiState
}

data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HomeScreenViewModel : ViewModel() {

    var anthropicUiState: AnthropicUiState by mutableStateOf(AnthropicUiState.Success(emptyList()))
        private set

    var inputText by mutableStateOf("")
        private set
    fun updateInputText(new: String) {
        inputText = new
    }

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: SnapshotStateList<ChatMessage> = _messages

    fun sendMessage() {

        viewModelScope.launch {

            if (inputText.isBlank()) {
                return@launch
            }

            val userMessage = ChatMessage(role = "user", content = inputText)

            _messages.add(userMessage)
            updateUiState()

            //anthropicUiState = AnthropicUiState.Loading

            val request = AnthropicRequest(
                model = "claude-3-5-sonnet-20240620",
                maxTokens = 1024,
                messages = _messages.map { Message(role = it.role, content = it.content) }
            )

            try {
                val chatResult = AnthropicApi.retrofitService.sendMessage(request)
                val assistantMessage = ChatMessage(
                    role = "assistant",
                    content = chatResult.content.firstOrNull()?.text ?: ""
                )
                _messages.add(assistantMessage)
                updateUiState()
            } catch (e: Exception) {
                anthropicUiState = AnthropicUiState.Error("Error: ${e.message}")
            }
            inputText = ""
        }
    }

    private fun updateUiState() {
        anthropicUiState = AnthropicUiState.Success(_messages.toList())
    }
}

fun Message.toChatMessage() = ChatMessage(role = role, content = content)

fun ChatMessage.toMessage() = Message(role = role, content = content)

fun AnthropicResponse.toChatMessage() = ChatMessage(
    role = role,
    content = content.firstOrNull()?.text ?: ""
)