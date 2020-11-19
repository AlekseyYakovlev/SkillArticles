package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.RegistrationReq
import javax.inject.Inject

class RootRepository @Inject constructor(
    private val preferences: PrefManager,
    private val network: RestService,
) {
    fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    suspend fun login(login: String, pass: String) {
        val auth = network.login(LoginReq(login, pass))
        auth.user?.let { preferences.profile = it }
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

    suspend fun register(name: String, login: String, password: String) {
        val auth = network.register(RegistrationReq(name, login, password))
        auth.user?.let { preferences.profile = it }
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }
}