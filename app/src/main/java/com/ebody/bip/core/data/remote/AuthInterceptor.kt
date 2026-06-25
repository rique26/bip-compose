package com.ebody.bip.core.data.remote

import com.ebody.bip.features.auth.domain.repository.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Não adicionar token para rotas de auth
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return chain.proceed(originalRequest)
        }

        // Obtém o token síncronamente de forma segura a partir do Flow
        val token = runBlocking {
            sessionManager.getUserSession().first().idToken
        }

        if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(originalRequest)
    }
}