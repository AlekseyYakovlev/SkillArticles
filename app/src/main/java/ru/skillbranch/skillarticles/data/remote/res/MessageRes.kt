package ru.skillbranch.skillarticles.data.remote.res

import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
data class MessageRes(
    val message: CommentRes,
    val messageCount: Int
)