package com.ebody.bip.features.wellbeing.data.repository

import android.util.Log
import com.ebody.bip.features.auth.domain.repository.SessionManager
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodLocalDataSource
import com.ebody.bip.features.wellbeing.data.datasource.remote.MoodRemoteDataSource
import com.ebody.bip.features.wellbeing.data.mapper.toDomain
import com.ebody.bip.features.wellbeing.data.mapper.toEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class MoodRepositoryImpl @Inject constructor(
    private val localDataSource: MoodLocalDataSource,
    private val remoteDataSource: MoodRemoteDataSource,
    private val sessionManager: SessionManager
) : MoodRepository {

    companion object {
        private const val TAG = "MoodRepository"
    }

    private suspend fun getCurrentUserId(): String? {
        return sessionManager.getUserSession().firstOrNull()?.userId
    }

    override suspend fun saveMood(mood: MoodEntry) {
        Log.d(TAG, "Salvando registro localmente (Room)...")
        localDataSource.insertMood(mood.toEntity())

        getCurrentUserId()?.let { userId ->
            try {
                remoteDataSource.saveMoodRemote(userId, mood)
                Log.i(TAG, "Sincronização remota realizada com sucesso.")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao sincronizar registro com Firebase", e)
            }
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


    override suspend fun syncWithRemote() {
        getCurrentUserId()?.let { userId ->
            try {
                val remoteMoods = remoteDataSource.fetchAllMoods(userId)
                remoteMoods.forEach { remoteMood ->
                    localDataSource.insertMood(remoteMood.toEntity())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao sincronizar dados do Firebase na abertura", e)
            }
        }
    }
}