package ru.skillbranch.skillarticles.data.remote.req

data class RegistrationReq(
    val name: String,
    val email: String,
    val password: String
)