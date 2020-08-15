package ru.skillbranch.skillarticles.data.remote.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorRes(
    val id: String,
    val avatar: String,
    val name: String
)