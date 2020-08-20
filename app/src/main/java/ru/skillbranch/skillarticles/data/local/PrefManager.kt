package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.JsonConverter.moshi
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveObjDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefObjDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.User


object PrefManager {
    private const val IS_DARK_MODE = "isDarkMode"
    private const val IS_BIG_TEXT = "isBigText"
    private const val PROFILE = "profile"
    private const val ACCESS_TOKEN = "accessToken"


    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)
    var accessToken by PrefDelegate("")
    var refreshToken by PrefDelegate("")
    var profile: User? by PrefObjDelegate(moshi.adapter(User::class.java))

    val isAuthLive by lazy {
        val token by PrefLiveDelegate(ACCESS_TOKEN, "", preferences)
        token.map { it.isNotEmpty() }
    }
    val profileLive by PrefLiveObjDelegate(PROFILE, moshi.adapter(User::class.java), preferences)


    val appSettings = MediatorLiveData<AppSettings>().apply {
        val isDarkModeLive by PrefLiveDelegate(IS_DARK_MODE, false, preferences)
        val isBigTextLive by PrefLiveDelegate(IS_BIG_TEXT, false, preferences)
        value = AppSettings()

        addSource(isDarkModeLive) {
            value = value!!.copy(isDarkMode = it)
        }

        addSource(isBigTextLive) {
            value = value!!.copy(isBigText = it)
        }

    }.distinctUntilChanged()

    fun setAppSettings(appSettings: AppSettings) {
        isDarkMode = appSettings.isDarkMode
        isBigText = appSettings.isBigText
    }

//    private val _isAuth = MutableLiveData(false)
//    private val _appSettings =
//        MutableLiveData(AppSettings(isDarkMode = false, isBigText = false))
//
//    val isDarkMode: Boolean? = _appSettings.value?.isDarkMode
//
//    var profile: User? by PrefObjDelegate(moshi.adapter(User::class.java))
//
//    init {
//        _isAuth.postValue(preferences.getBoolean(IS_AUTH, false))
//        _appSettings.postValue(
//            AppSettings(
//                isDarkMode = preferences.getBoolean(IS_DARK_MODE, false),
//                isBigText = preferences.getBoolean(IS_BIG_TEXT, false)
//            )
//        )
//    }
//
//    fun clearAll() {
//        preferences.edit().clear().apply()
//    }
//
//    fun getAppSettings(): LiveData<AppSettings> = _appSettings.distinctUntilChanged()
//
//    fun setAppSettings(appSettings: AppSettings) {
//        _appSettings.postValue(appSettings)
//        preferences.edit()
//            .putBoolean(IS_DARK_MODE, appSettings.isDarkMode)
//            .putBoolean(IS_BIG_TEXT, appSettings.isBigText)
//            .apply()
//    }
//
//    fun isAuth(): LiveData<Boolean> = _isAuth
//
//    fun setAuth(isAuth: Boolean) {
//        _isAuth.postValue(isAuth)
//        preferences.edit().putBoolean(IS_AUTH, isAuth).apply()
//    }
}