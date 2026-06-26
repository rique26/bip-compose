package com.ebody.bip.features.schedule.data.datasource.remote

import android.util.Log
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.schedule.data.mapper.toDomain
import com.ebody.bip.features.schedule.data.mapper.toRemote
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.model.ScheduleError
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class MedicationFirebaseDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MedicationFirebaseDataSource {
    companion object {
        private const val TAG = "FirebaseDataSource"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_REMINDERS = "reminders"
        private const val NETWORK_TIMEOUT = 5000L // 5 segundos de limite
    }

    override suspend fun syncReminder(userId: String, reminder: MedicationReminder): Result<Unit, ScheduleError> {
        return try {
            Log.d(TAG, "Tentando sincronizar no Firestore - UserId: $userId, ReminderId: ${reminder.id}")

            val remoteModel = reminder.toRemote()

            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_REMINDERS)
                .document(reminder.id.toString())
                .set(remoteModel)
                .await()

            Log.d(TAG, "Sincronização no Firestore realizada com sucesso!")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao sincronizar no Firestore: ${e.message}", e)
            Result.Error(ScheduleError.Unknown(e.localizedMessage))
        }
    }

    override suspend fun deleteReminder(userId: String, reminderId: Long): Result<Unit, ScheduleError> {
        return try {
            Log.d(TAG, "Removendo do Firestore - UserId: $userId, ReminderId: $reminderId")
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_REMINDERS)
                .document(reminderId.toString())
                .delete()
                .await()
            Log.d(TAG, "Remoção no Firestore concluída com sucesso.")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao remover do Firestore: ${e.message}", e)
            Result.Error(ScheduleError.Unknown(e.localizedMessage))
        }
    }

    override suspend fun fetchAllReminders(userId: String): Result<List<MedicationReminder>, ScheduleError> {
        return try {
            Log.d(TAG, "Buscando todos os lembretes do Firestore para UserId: $userId")
            val snapshot = withTimeout(NETWORK_TIMEOUT) {
                firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_REMINDERS)
                    .get()
                    .await()
            }

            val reminders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MedicationReminderRemote::class.java)?.toDomain()
            }

            Log.d(TAG, "Busca no Firestore retornou ${reminders.size} lembretes.")
            Result.Success(reminders)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao buscar todos do Firestore: ${e.message}", e)
            Result.Error(ScheduleError.Unknown(e.localizedMessage))
        }
    }
}