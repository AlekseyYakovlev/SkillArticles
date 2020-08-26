package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq

class TokenAuthenticator : Authenticator {
    private val prefs = PrefManager
    private val api by lazy { NetworkManager.api }

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
        val response = api.refreshToken(RefreshReq(oldRefreshToken)).execute()
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