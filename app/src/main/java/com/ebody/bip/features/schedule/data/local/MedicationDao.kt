package com.ebody.bip.features.schedule.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE name LIKE :query || '%' LIMIT 30")
    suspend fun searchMedications(query: String): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedicationsInBulk(medications: List<MedicationEntity>)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Long): MedicationEntity?

}