package com.ebody.bip.core.data.intelligence.repository

import android.content.Context
import android.util.Log
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmInferenceEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LlmInferenceEngine {

    companion object {
        private const val TAG = "BipAI_LlmEngine"
        private const val MODEL_NAME = "gemma3-1b-it-int4.task"
    }

    @Volatile
    private var llmInference: LlmInference? = null

    @Volatile
    private var isInitializing = false

    override suspend fun initialize() = withContext(Dispatchers.IO) {

        if (llmInference != null) {
            Log.i(TAG, "LLM já inicializado.")
            return@withContext
        }

        if (isInitializing) {
            Log.i(TAG, "LLM já está inicializando.")
            return@withContext
        }

        isInitializing = true

        try {
            Log.i(TAG, "========== INICIANDO LLM ==========")

            val start = System.currentTimeMillis()

            val modelPath = getModelPath()

            Log.i(TAG, "Criando LlmInferenceOptions...")

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .build()

            llmInference = LlmInference.createFromOptions(
                context,
                options
            )

            Log.i(TAG, "LLM inicializado em ${System.currentTimeMillis() - start} ms")

        } catch (t: Throwable) {
            Log.e(TAG, "Falha ao inicializar LLM", t)
        } finally {
            isInitializing = false
        }
    }

    override suspend fun generateResponse(
        systemPrompt: String,
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {

        if (llmInference == null) {
            initialize()
        }

        val engine = llmInference
            ?: throw IllegalStateException("LLM não inicializado.")

        // Verifica qual o tipo de resposta esperado para montar o prompt correto
        val prompt = if (systemPrompt.contains("RESUMO CLÍNICO DE ACOMPANHAMENTO")) {
            """
            <start_of_turn>system
            $systemPrompt
            <end_of_turn>
            <start_of_turn>user
            $userPrompt
            <end_of_turn>
            <start_of_turn>model
            """.trimIndent()
        } else {
            // Prompt da análise de risco
            """
            <start_of_turn>system
            $systemPrompt
            <end_of_turn>
            <start_of_turn>user
            $userPrompt
            <end_of_turn>
            <start_of_turn>model
            { "riskLevel": 
            """.trimIndent()
        }

        Log.i(TAG, "Gerando resposta...")

        val response = engine.generateResponse(prompt)

        Log.i(TAG, "Resposta gerada.")

        response
    }

    private fun getModelPath(): String {

        val file = File(context.filesDir, MODEL_NAME)

        if (!file.exists()) {

            Log.i(TAG, "Copiando modelo dos assets...")

            context.assets.open(MODEL_NAME).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Log.i(TAG, "Modelo copiado.")
        } else {
            Log.i(TAG, "Modelo já existe.")
        }

        return file.absolutePath
    }
}