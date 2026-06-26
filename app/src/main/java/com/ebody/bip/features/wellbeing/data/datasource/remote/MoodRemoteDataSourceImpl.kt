package com.ebody.bip.features.wellbeing.data.datasource.remote

import android.util.Log
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.data.mapper.toDomain
import com.ebody.bip.features.wellbeing.data.mapper.toRemoteEntity
import com.ebody.bip.features.wellbeing.data.model.MoodRemoteEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.model.WellbeingError
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class MoodRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MoodRemoteDataSource {

    companion object {
        private const val TAG = "MoodRemoteDataSource"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_MOODS = "moods"
        private const val NETWORK_TIMEOUT = 5000L // 5 segundos de limite
    }

    override suspend fun syncMood(userId: String, mood: MoodEntry) : Result<Unit, WellbeingError> {
        return try {
            Log.d(TAG, "Tentando sincronizar no Firestore - UserId: $userId, MoodId: ${mood.id}")

            val remoteModel = mood.toRemoteEntity()

            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_MOODS)
                .document(mood.id.toString())
                .set(remoteModel)
                .await()

            Log.d(TAG, "Sincronização no Firestore realizada com sucesso!")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao sincronizar no Firestore: ${e.message}", e)
            Result.Error(WellbeingError.Unknown(e.localizedMessage))
        }
    }

    override suspend fun fetchAllMoods(userId: String): Result<List<MoodEntry>, WellbeingError> {
        return try {
            Log.d(TAG, "Buscando todos os registros do Firestore para UserId: $userId")
            val snapshot = withTimeout(NETWORK_TIMEOUT) {
                firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_MOODS)
                    .get()
                    .await()
            }

            val moods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MoodRemoteEntity::class.java)?.toDomain()
            }

            Log.d(TAG, "Busca no Firestore retornou ${moods.size} registros.")
            Result.Success(moods)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao buscar todos do Firestore: ${e.message}", e)
            Result.Error(WellbeingError.Unknown(e.localizedMessage))
        }
    }
}