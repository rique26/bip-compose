package com.ebody.bip.features.wellbeing.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoods(moods: List<MoodEntity>)

    @Query("SELECT * FROM mood_table ORDER BY timestamp DESC")
    fun getMoodHistory(): Flow<List<MoodEntity>>

    @Query("SELECT * FROM mood_table WHERE timestamp BETWEEN :start AND :end")
    suspend fun getMoodsBetween(start: String, end: String): List<MoodEntity>

    @Query("SELECT * FROM mood_table ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLastMoods(limit: Int): List<MoodEntity>
}