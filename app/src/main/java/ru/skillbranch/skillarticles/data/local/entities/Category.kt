package ru.skillbranch.skillarticles.data.local.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_categories")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val icon: String,
    val title: String
)

data class CategoryData(
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val icon: String,
    val title: String,
    @ColumnInfo(name = "articles_count")
    val articlesCount: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryId)
        parcel.writeString(icon)
        parcel.writeString(title)
        parcel.writeInt(articlesCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryData> {
        override fun createFromParcel(parcel: Parcel): CategoryData {
            return CategoryData(parcel)
        }

        override fun newArray(size: Int): Array<CategoryData?> {
            return arrayOfNulls(size)
        }
    }
}