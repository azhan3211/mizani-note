package com.mizani.note.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mizani.note.data.dao.CategoryDao
import com.mizani.note.data.dao.NoteDao
import com.mizani.note.data.entities.NoteCategoryEntity
import com.mizani.note.data.entities.NoteEntity


@Database(
    entities = [
        NoteEntity::class,
        NoteCategoryEntity::class
   ],
    version = 1
)
@TypeConverters(RoomConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getNote(): NoteDao
    abstract fun getCategory(): CategoryDao

}