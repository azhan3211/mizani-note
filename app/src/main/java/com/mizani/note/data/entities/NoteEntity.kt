package com.mizani.note.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date")
    val date: Date,
    @ColumnInfo(name = "color")
    val color: String,
    @ColumnInfo(name = "is_repeated")
    val isRepeated: Boolean = false,
    @ColumnInfo(name = "is_reminder_set")
    val isReminderSet: Boolean = false,
    @ColumnInfo(name = "category")
    val category: Long
)