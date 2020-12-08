package ru.skillbranch.skillarticles.data.remote.req

data class MessageReq(
    val message: String,
    val answerTo: String? = null
)