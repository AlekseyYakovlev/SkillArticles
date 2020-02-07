package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {


    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        val name = property.name
        val prefs = thisRef.preferences

        return when (defaultValue) {
            is Boolean -> (prefs.getBoolean(name, defaultValue) as? T) ?: defaultValue
            is String -> (prefs.getString(name, defaultValue) as? T) ?: defaultValue
            is Float -> (prefs.getFloat(name, defaultValue) as? T) ?: defaultValue
            is Int -> (prefs.getInt(name, defaultValue) as? T) ?: defaultValue
            is Long -> (prefs.getLong(name, defaultValue) as? T) ?: defaultValue
            else -> throw NotFoundRealizationException(defaultValue)
        }
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        val name = property.name
        with(thisRef.preferences.edit()) {
            when(value){
                is Boolean -> putBoolean(name, value)
                is String -> putString(name, value)
                is Float -> putFloat(name, value)
                is Int -> putInt(name, value)
                is Long -> putLong(name, value)
                else -> throw NotFoundRealizationException(value)
            }
            apply()
        }
    }

    class NotFoundRealizationException(defaultValue: Any?) :
        Exception("not found realization for $defaultValue")
}