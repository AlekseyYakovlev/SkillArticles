package ru.skillbranch.skillarticles.data.remote.req

//@JsonClass(generateAdapter = true)
data class LoginReq(
    val login: String,
    val password: String
)