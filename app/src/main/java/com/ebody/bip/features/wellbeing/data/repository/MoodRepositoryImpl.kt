package com.ebody.bip.features.wellbeing.data.repository

import android.util.Log
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.repository.SessionManager
import com.ebody.bip.features.schedule.data.mapper.toEntity
import com.ebody.bip.features.schedule.data.repository.ReminderRepositoryImpl
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
import kotlin.collections.map

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
        val insertedId = localDataSource.insertMood(mood)

        val savedMood = if (mood.id == 0L && insertedId > 0) {
            mood.copy(id = insertedId)
        } else {
            mood
        }

        getCurrentUserId()?.let { userId ->
            Log.d(TAG, "Usuário logado ($userId). Iniciando sincronização remota...")
            val result = remoteDataSource.syncMood(userId, savedMood)

            when (result) {
                is Result.Success -> Log.i(TAG, "Sincronização remota (Firestore) aceita com sucesso.")
                is Result.Error -> Log.e(TAG, "Falha na sincronização remota.")
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
            when (val remoteResult = remoteDataSource.fetchAllMoods(userId)) {
                is Result.Success -> {
                    val remoteData = remoteResult.data.map { it.toEntity() }
                    localDataSource.insertMoods(remoteData)
                }
                is Result.Error -> Log.e(TAG, "Erro ao sincronizar do remoto para local.")
            }
        }
    }
}