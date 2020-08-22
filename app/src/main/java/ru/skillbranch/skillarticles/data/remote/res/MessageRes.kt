package ru.skillbranch.skillarticles.data.remote.res

//@JsonClass(generateAdapter = true)
data class MessageRes(
    val message: CommentRes,
    val messageCount: Int
)