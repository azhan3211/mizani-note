package com.mizani.note.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class NoteCategoryEntity(
    @PrimaryKey(true)
    val id: Long,
    @ColumnInfo("name")
    val name: String
)