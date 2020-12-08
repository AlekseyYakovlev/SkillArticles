package ru.skillbranch.skillarticles.data.remote.res


data class ProfileRes(
    val updatedAt: Long? = null,
    val id: String,
    val avatar: String,
    val name: String,
    val about: String,
    val rating: Int,
    val respect: Int
)