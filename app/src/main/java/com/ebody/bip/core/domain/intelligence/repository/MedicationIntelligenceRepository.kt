package com.ebody.bip.core.domain.intelligence.repository

interface MedicationIntelligenceRepository {
    /**
     * Retorna a porcentagem de adesão aos medicamentos nos últimos X dias (ex: 0.8 para 80%).
     */
    suspend fun getAdherenceRateLastDays(days: Int): Float

    /**
     * Registra que o medicamento com o ID do lembrete foi tomado.
     */
    suspend fun markMedicationAsTaken(reminderId: Long)
}