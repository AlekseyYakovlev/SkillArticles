package ru.skillbranch.skillarticles.data.remote.res

import com.squareup.moshi.Json
import ru.skillbranch.skillarticles.data.models.User
import java.util.*

//@JsonClass(generateAdapter = true)
data class CommentRes(
    val id: String,
    @Json(name = "author")
    val user: User,
    @Json(name = "message")
    val body: String,
    val date: Date,
    val slug: String,
    val answerTo: String? = null
)