package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor

class App() : Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            if (instance == null) instance = App()
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        NetworkMonitor.registerNetworkMonitor(applicationContext)

        val mode = if (PrefManager.isDarkMode==true) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}