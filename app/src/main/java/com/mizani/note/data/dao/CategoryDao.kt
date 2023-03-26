package com.mizani.note.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mizani.note.data.entities.NoteCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table")
    fun getAll(): Flow<List<NoteCategoryEntity>>

    @Query("DELETE FROM category_table WHERE id=:id")
    fun delete(id: Long)

    @Insert
    fun insert(categoryEntity: NoteCategoryEntity): Long

}