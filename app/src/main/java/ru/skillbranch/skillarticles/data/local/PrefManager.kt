package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.models.AppSettings


class PrefManager(context: Context) {
    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.instance.applicationContext)
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

    fun getAppSettings(): LiveData<AppSettings> =_appSettings

    fun isAuth(): LiveData<Boolean> = _isAuth

    fun setAuth(isAuth: Boolean): Unit {
        _isAuth.postValue(isAuth)
        preferences.edit().putBoolean(IS_AUTH, isAuth).apply()
    }

    companion object {
        const val IS_AUTH = "is_auth"
        const val IS_DARK_MODE = "is_dark_mode"
        const val IS_BIG_TEXT = "is_big_text"
    }
}