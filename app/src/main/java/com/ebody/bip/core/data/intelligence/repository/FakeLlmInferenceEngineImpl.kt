package com.ebody.bip.core.data.intelligence.repository

import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeLlmInferenceEngineImpl @Inject constructor() : LlmInferenceEngine {

    override suspend fun initialize() {
        // Sem comportamento necessário no fake
    }

    override suspend fun generateResponse(
        systemPrompt: String,
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {

        delay(2000)

        if (systemPrompt.contains("RESUMO CLÍNICO DE ACOMPANHAMENTO")) {
            """
            📊 RESUMO CLÍNICO DE ACOMPANHAMENTO
            
            Adesão Medicamentosa:
            • Taxa de Adesão Mapeada: 85% do período avaliado.
            • Intercorrências: Identificados 2 episódios de esquecimento completo e 1 relato de atraso superior a 6 horas.
            
            Volatilidade de Humor e Sintomas:
            • Predomínio: Estados classificados como "Estável" ou "Bom" cobrem 78% do histórico.
            • Instabilidade: 2 picos mapeados com elevação de risco para nível "Alerta".
            
            Correlação Clínica (Insights de IA):
            • Foi observada uma janela de correlação direta entre as falhas de dosagem e crises agudas. Nos dias de esquecimento, o paciente reportou sintomas vespertinos de irritabilidade e cefaleia nas notas textuais.
            • Fatores de estresse associados a "prazos acadêmicos" e "privação de sono" atuaram como gatilhos secundários evidentes.
            """.trimIndent()

        } else {

            // Retorno completando o JSON para a análise diária de risco (SaveMoodWithAiAnalysisUseCase)
            """
            "ALERTA",
              "instruction": "Percebi que o relato de cefaleia coincidiu com sua queixa de privação de sono. Evite telas à noite e tome o medicamento no horário regular."
            }
            """.trimIndent()
        }
    }
}