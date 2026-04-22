package org.cedarstar.android.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

fun interface TokenProvider {
    fun getToken(): String
}

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(ApiConstants.TOKEN_HEADER, tokenProvider.getToken())
            .build()
        return chain.proceed(request)
    }
}
