package com.ebody.bip.core.data.intelligence.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
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
        private const val MIN_RAM_GB = 2.5
    }

    @Volatile
    private var llmInference: LlmInference? = null
    @Volatile
    private var isInitializing = false

    override suspend fun initialize() = withContext(Dispatchers.IO) {

        if (!isDeviceCompatible()) {
            Log.w(TAG, "Dispositivo incompatível com IA On-Device (exige 64-bit e RAM >= ${MIN_RAM_GB}GB).")
            throw UnsupportedOperationException("Hardware incompatível para execução local do modelo Gemma.")
        }

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

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)

            Log.i(TAG, "LLM inicializado em ${System.currentTimeMillis() - start} ms")

        } catch (t: Throwable) {
            Log.e(TAG, "Falha ao inicializar LLM", t)
            throw t
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
        Log.i(TAG, "Resposta gerada. $response")
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

    fun isDeviceCompatible(): Boolean {
        val supports64Bit = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
        if (!supports64Bit) return false

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val totalRamGb = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        return totalRamGb >= MIN_RAM_GB
    }
}