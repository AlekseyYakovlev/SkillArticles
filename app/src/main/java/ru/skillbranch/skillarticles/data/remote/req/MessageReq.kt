package ru.skillbranch.skillarticles.data.remote.req

//@JsonClass(generateAdapter = true)
data class MessageReq(
    val message: String,
    val answerTo: String? = null
)