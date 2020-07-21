package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager

object RootRepository {

    fun isAuth(): LiveData<Boolean> = PrefManager.isAuth()
    fun setAuth(auth: Boolean) = PrefManager.setAuth(auth)
}