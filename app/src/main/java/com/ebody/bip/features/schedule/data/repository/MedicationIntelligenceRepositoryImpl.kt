package com.ebody.bip.features.schedule.data.repository

import android.util.Log
import com.ebody.bip.core.domain.intelligence.repository.MedicationIntelligenceRepository
import com.ebody.bip.features.schedule.data.datasource.local.MedicationLocalDataSource
import com.ebody.bip.features.schedule.data.model.MedicationTakenEntity
import java.time.LocalDate
import javax.inject.Inject

class MedicationIntelligenceRepositoryImpl @Inject constructor(
    private val localDataSource: MedicationLocalDataSource
) : MedicationIntelligenceRepository {

    override suspend fun getAdherenceRateLastDays(days: Int): Float {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (days * 86400000L) // 24h em milissegundos * dias

        val adherenceLogs = localDataSource.getRemindersStatusBetween(startTime, endTime)

        if (adherenceLogs.isEmpty()) return 1.0f // Sem registros, assume 100% para evitar alarmes falsos

        val takenCount = adherenceLogs.count { it }
        val totalCount = adherenceLogs.size

        return takenCount.toFloat() / totalCount.toFloat()
    }

    override suspend fun markMedicationAsTaken(reminderId: Long) {
        Log.d("MedicationIntelligence", "Registrando medicamento como tomado. ID do Lembrete: $reminderId")
        val log = MedicationTakenEntity(
            reminderId = reminderId,
            timestamp = System.currentTimeMillis(),
            isTaken = true
        )
        localDataSource.insertMedicationLog(log)
        Log.d("MedicationIntelligence", "Log de medicação salvo com sucesso no banco de dados.")
    }
}