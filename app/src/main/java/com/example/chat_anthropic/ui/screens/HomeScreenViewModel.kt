package com.example.chat_anthropic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    data class Success(val respuesta: String) : AnthropicUiState
    data class Error(val message: String) : AnthropicUiState
    object Loading : AnthropicUiState
}

class HomeScreenViewModel: ViewModel() {

    var anthropicUiState: AnthropicUiState by mutableStateOf(AnthropicUiState.Loading)
        private set

    var inputText by mutableStateOf("")
        private set
    fun updateInputText(new:String){
        inputText=new
    }

    fun postAnthropicChat() {
        viewModelScope.launch {

            anthropicUiState = AnthropicUiState.Loading

            val request = AnthropicRequest(
                model = "claude-3-5-sonnet-20240620",
                maxTokens = 1024,
                messages = listOf(Message(role = "user", content = inputText))
            )

            if (inputText.isBlank()) {
                anthropicUiState = AnthropicUiState.Error("El mensaje no puede estar vacío")
                return@launch
            }

            anthropicUiState = try {
                val chatResult = AnthropicApi.retrofitService.sendMessage(request)
                AnthropicUiState.Success(chatResult.toFormattedString())
            } catch (e: HttpException) {
                // Manejar error HTTP
                AnthropicUiState.Error("HTTP ${e.code()}: ${e.message()}")
            } catch (e: IOException) {
                // Manejar error de red
                AnthropicUiState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                // Manejar otros errores
                AnthropicUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun AnthropicResponse.toFormattedString(): String {
        return this.content.joinToString("") { it.text }.trim()
    }
//    fun AnthropicResponse.toFormattedString(): String {
//        return """
//        ID: ${this.id}
//        Role: ${this.role}
//        Content: ${this.content.joinToString { it.text }}
//        Model: ${this.model}
//        Stop Reason: ${this.stopReason}
//        Input Tokens: ${this.usage.inputTokens}
//        Output Tokens: ${this.usage.outputTokens}
//    """.trimIndent()
//    }

}