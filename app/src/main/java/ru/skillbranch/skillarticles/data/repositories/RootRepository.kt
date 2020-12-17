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
): IRepository {
    fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    /** Login to the server
     * @param login Login string
     * @param pass Password string
     * @return True if login successful and False if login failed
     **/
    suspend fun login(login: String, pass: String): Boolean {
        val auth = network.login(LoginReq(login, pass))
        auth.user?.let { preferences.profile = it }
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
        return (auth.user != null)
    }

    fun logout() {
        preferences.profile = null
        preferences.accessToken = ""
        preferences.refreshToken = ""
    }

    suspend fun register(name: String, login: String, password: String) {
        val auth = network.register(RegistrationReq(name, login, password))
        auth.user?.let { preferences.profile = it }
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }
}