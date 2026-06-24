package com.ebody.bip.features.wellbeing.data.datasource.remote

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import javax.inject.Inject

class MoodRemoteDataSourceImpl @Inject constructor(
    // private val firestore: FirebaseFirestore
) : MoodRemoteDataSource {

    override suspend fun saveMoodRemote(mood: MoodEntry) {
        // firestore.collection("moods").document(mood.id.toString()).set(mood).await()
    }

}