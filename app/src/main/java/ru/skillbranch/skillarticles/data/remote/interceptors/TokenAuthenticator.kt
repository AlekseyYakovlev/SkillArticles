package ru.skillbranch.skillarticles.data.remote.interceptors

import dagger.Lazy
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val prefs: PrefManager,
    private val lazyApi: Lazy<RestService>,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (prefs.accessToken.isNotEmpty() && response.code == 401) {
            getNewToken()?.let {
                response.request.newBuilder()
                    .header("Authorization", it)
                    .build()
            }
        } else null
    }

    private fun getNewToken(): String? {
        val oldRefreshToken = prefs.refreshToken
        val response = lazyApi.get().refreshToken(RefreshReq(oldRefreshToken)).execute()
        return if (response.isSuccessful) {
            response.body()?.let {
                val newAccessToken = "Bearer ${it.accessToken}"
                prefs.accessToken = newAccessToken
                prefs.refreshToken = it.refreshToken
                newAccessToken
            }
        } else null
    }
}