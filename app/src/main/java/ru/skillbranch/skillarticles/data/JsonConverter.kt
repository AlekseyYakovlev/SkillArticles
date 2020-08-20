package ru.skillbranch.skillarticles.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

object JsonConverter {

    //json converter
    val moshi: Moshi = Moshi.Builder()
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory()) // for reflection
        .build()

    class DateAdapter {
        @FromJson
        fun fromJson(timestamp: Long) = Date(timestamp)

        @ToJson
        fun toJson(date: Date) = date.time
    }
}

