package com.ebody.bip.core.data.intelligence.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LlmInferenceEngineImplTest {

    private lateinit var llmEngine: LlmInferenceEngineImpl

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        llmEngine = LlmInferenceEngineImpl(context)
    }

    @Test
    fun generateResponse_withRealModel_returnsNonEmptyString() = runBlocking {
        // Arrange: Inicializa o modelo Gemma na memória do dispositivo
        llmEngine.initialize()

        // Act: Roda uma inferência simples
        val response = llmEngine.generateResponse(
            systemPrompt = "Você é um assistente de teste.",
            userPrompt = "Responda apenas com a palavra OK."
        )

        // Assert
        assertNotNull(response)
        assertTrue(response.isNotBlank())
    }
}