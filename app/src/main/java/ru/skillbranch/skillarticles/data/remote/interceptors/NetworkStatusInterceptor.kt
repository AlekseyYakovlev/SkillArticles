package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError

class NetworkStatusInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkMonitor.isConnected) throw NoNetworkError()

        return chain.proceed(chain.request())
    }
}