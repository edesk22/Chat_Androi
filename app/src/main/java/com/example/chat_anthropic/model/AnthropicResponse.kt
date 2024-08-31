package com.example.chat_anthropic.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnthropicResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<Content>,
    val model: String,
    @SerializedName("stop_reason")
    val stopReason: String,
    @SerializedName("stop_sequence")
    val stopSequence: String?,
    val usage: Usage
)

@Serializable
data class Content(
    val type: String,
    val text: String
)

@Serializable
data class Usage(
    @SerializedName("input_tokens")
    val inputTokens: Int,
    @SerializedName("output_tokens")
    val outputTokens: Int
)
