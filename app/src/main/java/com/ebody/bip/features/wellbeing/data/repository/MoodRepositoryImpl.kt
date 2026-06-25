package com.ebody.bip.features.wellbeing.data.repository

import android.util.Log
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodDao
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodLocalDataSource
import com.ebody.bip.features.wellbeing.data.datasource.remote.MoodRemoteDataSource
import com.ebody.bip.features.wellbeing.data.mapper.toDomain
import com.ebody.bip.features.wellbeing.data.mapper.toEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class MoodRepositoryImpl @Inject constructor(
    private val localDataSource: MoodLocalDataSource,
    private val remoteDataSource: MoodRemoteDataSource
) : MoodRepository {
    override suspend fun saveMood(mood: MoodEntry) {
        localDataSource.insertMood(mood.toEntity())
        try {
            remoteDataSource.saveMoodRemote(mood)
        } catch (e: Exception) {
            Log.e("MoodRepository", "Erro ao sincronizar humor com Firebase", e)
        }
    }

    override fun getMoodHistory(): Flow<List<MoodEntry>> {
        return localDataSource.getMoodHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMoodsBetween(start: LocalDateTime, end: LocalDateTime): List<MoodEntry> {
        val entities = localDataSource.getMoodsBetween(start.toString(), end.toString())
        return entities.map { it.toDomain() }
    }


    // todo remote
    /*
    override suspend fun syncRemoteToLocal() {
        try {
            val remoteMoods = remoteDataSource.fetchAllMoods()
            remoteMoods.forEach { mood ->
                localDataSource.insertMood(mood.toEntity())
            }
        } catch (e: Exception) {
            Log.e("MoodRepository", "Falha ao sincronizar dados na abertura", e)
        }
    }
    */
}