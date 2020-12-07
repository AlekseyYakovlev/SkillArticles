package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveObjDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefObjDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.User


class PrefManager(
    val context: Context,
    val moshi: Moshi
) {
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

    fun replaceAvatarUrl(url: String) {
        profile = profile?.copy(avatar = url)
    }

    companion object {
        private const val IS_DARK_MODE = "isDarkMode"
        private const val IS_BIG_TEXT = "isBigText"
        private const val PROFILE = "profile"
        private const val ACCESS_TOKEN = "accessToken"
    }

}