package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.AuthRefresh
import java.io.IOException


class TokenAuthenticator : Authenticator {
    private val prefs = PrefManager


    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (prefs.accessToken.isNotEmpty() && response.code == 401){
            val updatedToken = getNewToken()
            response.request.newBuilder()
                .header("Authorization", updatedToken)
                .build()
        }else null

    }

    private fun getNewToken(): String {
        val network = NetworkManager.api
        val oldRefreshToken = prefs.refreshToken
        val response = network.refreshToken(AuthRefresh(oldRefreshToken) ).execute().body()!!
        val newAccessToken = "Bearer ${response.accessToken}"
        prefs.accessToken
        prefs.refreshToken = response.refreshToken

        return newAccessToken
    }
}