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
            // Analisa o que o usuário digitou nas notas para simular uma resposta sob medida
            val userNotes = userPrompt.lowercase()

            val (riskLevel, instruction) = when {
                userNotes.contains("esqueci") || userNotes.contains("remédio") || userNotes.contains("remedio") -> {
                    "ALERTA" to "Identifiquei uma quebra na sua rotina de medicação. Tente vincular o alarme do Bip a um hábito fixo, como escovar os dentes, para evitar novos esquecimentos."
                }
                userNotes.contains("cansado") || userNotes.contains("sono") || userNotes.contains("dormi") -> {
                    "ALERTA" to "O cansaço acumulado impacta diretamente seu bem-estar. Que tal priorizar uma higiene do sono hoje? Evite telas 30 minutos antes de deitar."
                }
                userNotes.contains("triste") || userNotes.contains("ansioso") || userNotes.contains("ansiedade") -> {
                    "CRITICO" to "Notei uma oscilação mais intensa no seu relato. Lembre-se de respirar fundo, focar no momento presente e, se precisar, procurar sua rede de apoio ou profissional."
                }
                userNotes.contains("estressado") || userNotes.contains("faculdade") || userNotes.contains("prova") -> {
                    "ALERTA" to "A rotina acadêmica pode ser exaustiva. Divida suas tarefas em blocos menores e faça pausas de 5 minutos a cada hora para descompressão."
                }
                else -> {
                    "ESTAVEL" to "Excelente registro! Seu padrão de humor demonstra estabilidade clínica. Continue mantendo a consistência nos horários e autocuidado."
                }
            }

            """
            {
              "riskLevel": "$riskLevel",
              "instruction": "$instruction"
            }
            """.trimIndent()
        }
    }
}