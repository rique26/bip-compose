package com.ebody.bip.features.wellbeing.data.datasource.remote

import android.util.Log
import com.ebody.bip.features.wellbeing.data.mapper.toDomain
import com.ebody.bip.features.wellbeing.data.mapper.toRemoteEntity
import com.ebody.bip.features.wellbeing.data.model.MoodRemoteEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MoodRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MoodRemoteDataSource {

    companion object {
        private const val TAG = "MoodRemoteDataSource"
    }

    override suspend fun saveMoodRemote(userId: String, mood: MoodEntry) {
        val remoteEntity = mood.toRemoteEntity()
        Log.d(TAG, "Salvando humor remoto para user: $userId com id: ${mood.id}")

        firestore.collection("users")
            .document(userId)
            .collection("moods")
            .document(mood.id.toString())
            .set(remoteEntity)
            .await()

        Log.d(TAG, "Humor salvo com sucesso no Firestore.")
    }

    override suspend fun fetchAllMoods(userId: String): List<MoodEntry> {
        val collectionPath = "users/$userId/moods"
        Log.d(TAG, "Buscando humores na coleção: $collectionPath")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("moods")
            .get()
            .await()

        Log.d(TAG, "Documentos brutos recuperados: ${snapshot.size()}")

        if (snapshot.isEmpty) {
            Log.w(TAG, "Nenhum documento encontrado na subcoleção 'moods' para o usuário $userId.")
            return emptyList()
        }

        return snapshot.documents.mapNotNull { doc ->
            Log.d(TAG, "Processando docId: ${doc.id} -> Dados: ${doc.data}")

            val remoteEntity = doc.toObject(MoodRemoteEntity::class.java)
            if (remoteEntity == null) {
                Log.e(TAG, "Falha ao converter documento ${doc.id} para MoodRemoteEntity!")
            }

            remoteEntity?.toDomain(doc.id).also { domainModel ->
                if (domainModel == null) {
                    Log.e(TAG, "Mapeamento para domínio falhou (null) no docId: ${doc.id}")
                } else {
                    Log.d(TAG, "Mapeado com sucesso para domínio: $domainModel")
                }
            }
        }
    }
}