package ru.skillbranch.skillarticles.data.remote.res


//@JsonClass(generateAdapter = true)
data class ProfileRes(
    val updatedAt: Long? = null,
    val id: String,
    val avatar: String,
    val name: String,
    val about: String,
    val rating: Int,
    val respect: Int
)