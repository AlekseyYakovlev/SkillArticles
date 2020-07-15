package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.models.AppSettings


object PrefManager {
    private const val IS_AUTH = "is_auth"
    private const val IS_DARK_MODE = "is_dark_mode"
    private const val IS_BIG_TEXT = "is_big_text"

    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    private val _isAuth = MutableLiveData(false)
    private val _appSettings =
        MutableLiveData(AppSettings(isDarkMode = false, isBigText = false))

    init {
        _isAuth.postValue(preferences.getBoolean(IS_AUTH, false))
        _appSettings.postValue(
            AppSettings(
                isDarkMode = preferences.getBoolean(IS_DARK_MODE, false),
                isBigText = preferences.getBoolean(IS_BIG_TEXT, false)
            )
        )
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> = _appSettings

    fun setAppSettings(appSettings: AppSettings): Unit {
        _appSettings.postValue(appSettings)
        preferences.edit()
            .putBoolean(IS_DARK_MODE, appSettings.isDarkMode)
            .putBoolean(IS_BIG_TEXT, appSettings.isBigText)
            .apply()
    }

    fun isAuth(): LiveData<Boolean> = _isAuth

    fun setAuth(isAuth: Boolean): Unit {
        _isAuth.postValue(isAuth)
        preferences.edit().putBoolean(IS_AUTH, isAuth).apply()
    }
}