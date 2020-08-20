package ru.skillbranch.skillarticles.data.delegates

import com.squareup.moshi.JsonAdapter
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        property: KProperty<*>
    ): ReadWriteProperty<PrefManager, T> {
        val key = property.name
        return object : ReadWriteProperty<PrefManager, T> {

            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T {
                val prefs = thisRef.preferences

                if (storedValue == null) {
                    @Suppress("UNCHECKED_CAST")
                    storedValue = when (defaultValue) {
                        is Boolean -> prefs.getBoolean(key, defaultValue as Boolean) as T
                        is String -> prefs.getString(key, defaultValue as String) as T
                        is Float -> prefs.getFloat(key, defaultValue as Float) as T
                        is Int -> prefs.getInt(key, defaultValue as Int) as T
                        is Long -> prefs.getLong(key, defaultValue as Long) as T
                        else -> throw NotFoundRealizationException(defaultValue)
                    }
                }
                return storedValue!!
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T) {
                with(thisRef.preferences.edit()) {
                    when (value) {
                        is Boolean -> putBoolean(key, value)
                        is String -> putString(key, value)
                        is Float -> putFloat(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        else -> throw NotFoundRealizationException(value)
                    }
                    apply()
                }
                storedValue = value
            }

        }
    }


    class NotFoundRealizationException(value: Any?) :
        Exception("Not found realization for $value")
}

class PrefObjDelegate<T>(
    private val adapter: JsonAdapter<T>
) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        property: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key = property.name
        return object : ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) {
                    storedValue = thisRef.preferences.getString(key, null)
                        ?.let { adapter.fromJson(it) }
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                storedValue = value
                thisRef.preferences.edit()
                    .putString(key, value?.let { adapter.toJson(it) })
                    .apply()
            }
        }
    }
}