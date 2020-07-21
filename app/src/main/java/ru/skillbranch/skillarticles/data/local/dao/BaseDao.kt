package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.*

@Dao
interface BaseDao<T : Any> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: T): Long

    @Update
    fun update(list: List<T>)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}