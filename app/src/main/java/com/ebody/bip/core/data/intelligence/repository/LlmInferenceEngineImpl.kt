package com.ebody.bip.core.data.intelligence.repository

import android.content.Context
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmInferenceEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LlmInferenceEngine {

    // Inicializa a inferência do MediaPipe LLM apontando para o arquivo de modelo .bin nos assets
    private val llmInference: LlmInference by lazy {
        LlmInference.createFromOptions(
            context,
            LlmInference.LlmInferenceOptions.builder()
                .setModelPath("gemma3-1b-it-int4.bin")
                .setMaxTokens(512)
                .setTopK(40)
                .setTemperature(0.2f)
                .build()
        )
    }

    override suspend fun generateResponse(systemPrompt: String, userPrompt: String): String {
        val fullPrompt = "<start_of_turn>system\n$systemPrompt<end_of_turn>\n<start_of_turn>user\n$userPrompt<end_of_turn>\n<start_of_turn>model\n"
        return llmInference.generateResponse(fullPrompt)
    }
}