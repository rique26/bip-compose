package com.ebody.bip.core.data.remote

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManager
import com.ebody.bip.features.auth.domain.repository.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val firebaseAuthManager: FirebaseAuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // Se receber 401, tentar renovar token
        if (response.code == 401) {
            synchronized(this) {
                val newToken = runBlocking {
                    when (val result = firebaseAuthManager.getIdToken(forceRefresh = true)) {
                        is Result.Success -> {
                            // Atualiza o token unificado no DataStore local via SessionManager
                            sessionManager.updateIdToken(result.data)
                            result.data
                        }
                        else -> null
                    }
                }

                if (newToken != null) {
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    response.close()
                    return chain.proceed(newRequest)
                }
            }
        }

        return response
    }
}