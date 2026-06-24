package com.ebody.bip.features.schedule.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebody.bip.features.schedule.data.model.MedicationEntity

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE name LIKE :query || '%' LIMIT 30")
    suspend fun searchMedications(query: String): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedicationsInBulk(medications: List<MedicationEntity>)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Long): MedicationEntity?

}