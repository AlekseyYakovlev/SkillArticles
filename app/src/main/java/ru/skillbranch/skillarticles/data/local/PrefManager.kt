package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    val preferences: SharedPreferences

    init {
        preferences = PrefManager(context).preferences
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }
}