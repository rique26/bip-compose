package com.ebody.bip.core.data.remote

import com.ebody.bip.features.auth.data.datasource.local.AuthDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Não adicionar token para rotas de auth
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return chain.proceed(originalRequest)
        }

        // Adicionar token ao header Authorization
        val token = runBlocking {
            authDataStore.getIdToken().let { flow ->
                var result: String? = null
                runBlocking {
                    flow.collect { result = it }
                }
                result
            }
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