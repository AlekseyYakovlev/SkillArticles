package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context

class App() : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            if (instance == null) instance = App()
            return instance!!.applicationContext
        }
    }
}