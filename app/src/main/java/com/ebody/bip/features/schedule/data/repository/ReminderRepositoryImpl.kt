package com.ebody.bip.features.schedule.data.repository

import android.util.Log
import com.ebody.bip.features.auth.domain.repository.SessionManager
import com.ebody.bip.features.schedule.data.datasource.local.MedicationLocalDataSource
import com.ebody.bip.features.schedule.data.datasource.remote.MedicationFirebaseDataSource
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.schedule.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val localDataSource: MedicationLocalDataSource,
    private val remoteDataSource: MedicationFirebaseDataSource,
    private val sessionManager: SessionManager
) : ReminderRepository {

    companion object {
        private const val TAG = "ReminderRepository"
    }

    private suspend fun getCurrentUserId(): String? {
        return sessionManager.getUserSession().firstOrNull()?.userId
    }

    override suspend fun saveReminder(reminder: MedicationReminder) {
        Log.d(TAG, "Salvando localmente (Room)...")
        val insertedId = localDataSource.insertReminder(reminder)

        val savedReminder = if (reminder.id == 0L && insertedId > 0) {
            reminder.copy(id = insertedId)
        } else {
            reminder
        }

        getCurrentUserId()?.let { userId ->
            Log.d(TAG, "Usuário logado ($userId). Iniciando sincronização remota...")
            val result = remoteDataSource.syncReminder(userId, savedReminder)

            when (result) {
                is Result.Success -> Log.i(TAG, "Sincronização remota (Firestore) aceita com sucesso.")
                is Result.Error -> Log.e(TAG, "Falha na sincronização remota.")
            }
        }
    }

    override suspend fun syncWithRemote() {
        getCurrentUserId()?.let { userId ->
            when (val remoteResult = remoteDataSource.fetchAllReminders(userId)) {
                is Result.Success -> {
                    val remoteData = remoteResult.data.map { it.toEntity() }
                    localDataSource.insertReminders(remoteData)
                }
                is Result.Error -> Log.e(TAG, "Erro ao sincronizar do remoto para local.")
            }
        }
    }

    override fun getReminders(): Flow<List<MedicationReminder>> {
        return localDataSource.getActiveReminders()
    }

    override suspend fun deleteReminder(reminder: MedicationReminder) {
        localDataSource.deleteReminder(reminder)
        getCurrentUserId()?.let { userId ->
            remoteDataSource.deleteReminder(userId, reminder.id)
        }
    }

}
