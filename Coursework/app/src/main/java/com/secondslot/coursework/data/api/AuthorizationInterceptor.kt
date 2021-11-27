package com.secondslot.coursework.data.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val newRequest: Request = originalRequest.newBuilder()
            .header("Authorization", Credentials.basic(EMAIL, API_KEY))
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        private const val EMAIL = "secondslot@gmail.com"
        private const val API_KEY = "S6KeF3KYaThenAPdRVoXomUwv064riSQ"
    }
}
