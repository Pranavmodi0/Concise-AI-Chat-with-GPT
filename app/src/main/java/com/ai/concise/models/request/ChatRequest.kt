package com.ai.concise.models.request

data class ChatRequest(
    val max_tokens: Int,
    val model: String,
    val prompt: String,
    val temperature: Double
)