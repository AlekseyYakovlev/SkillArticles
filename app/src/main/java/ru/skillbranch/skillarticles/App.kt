package ru.skillbranch.skillarticles

import android.app.Application

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        // fun applicationContext(): Context = instance.applicationContext
    }

    init {
        instance = this
    }

}