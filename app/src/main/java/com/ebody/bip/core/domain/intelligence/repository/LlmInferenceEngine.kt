package com.ebody.bip.core.domain.intelligence.repository

interface LlmInferenceEngine {
    suspend fun generateResponse(systemPrompt: String, userPrompt: String): String
}