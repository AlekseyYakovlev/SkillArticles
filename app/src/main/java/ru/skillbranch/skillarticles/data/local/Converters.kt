package ru.skillbranch.skillarticles.data.local

import androidx.room.TypeConverter
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import java.util.*

class DateConverter {
    @TypeConverter
    fun timestampToDate(timestamp:Long):Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date:Date):Long = date.time
}

class MarkdownConverter{
    @TypeConverter
    fun toMarkdown(content:String?): List<MarkdownElement>?=
        content?.let { MarkdownParser.parse(it) }
}