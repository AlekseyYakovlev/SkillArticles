package ru.skillbranch.skillarticles.data.remote.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryRes(
    val id: String,
    val title: String,
    val icon: String
)