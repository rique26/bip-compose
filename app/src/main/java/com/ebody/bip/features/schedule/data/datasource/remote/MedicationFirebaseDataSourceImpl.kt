package com.ebody.bip.features.schedule.data.datasource.remote

import com.ebody.bip.features.schedule.domain.datasource.MedicationRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MedicationFirebaseDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MedicationRemoteDataSource {
//    override suspend fun fetchRemoteMedications(): List<MedicationDto> {
//        return firestore.collection("medications").get().await().toObjects(MedicationDto::class.java)
//    }
}