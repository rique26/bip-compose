package com.ebody.bip.features.schedule.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ebody.bip.features.schedule.domain.model.Medication

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM medication_reminders")
    suspend fun getAllReminders(): List<ReminderEntity>

    // Busca os remédios que começam com o texto digitado (Limitado a 30 para performance)
    @Query("SELECT * FROM medications WHERE name LIKE :query || '%' LIMIT 30")
    suspend fun searchMedications(query: String): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedicationsInBulk(medications: List<MedicationEntity>)
}