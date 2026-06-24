package com.ebody.bip.features.emergency.domain.usecase

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.ebody.bip.features.emergency.domain.model.Coordinates
import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class TriggerEmergencyUseCase @Inject constructor(
    private val repository: EmergencyRepository,
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend operator fun invoke() {
        val coordinates = fetchCurrentLocation()
        repository.sendEmergencySignal(
            latitude = coordinates?.latitude,
            longitude = coordinates?.longitude
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchCurrentLocation(): Coordinates? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(Coordinates(location.latitude, location.longitude))
            } else {
                // Fallback para requisição de localização caso o cache esteja vazio
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setWaitForAccurateLocation(true)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        fusedLocationClient.removeLocationUpdates(this)
                        val loc = locationResult.lastLocation
                        if (loc != null) {
                            continuation.resume(Coordinates(loc.latitude, loc.longitude))
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}