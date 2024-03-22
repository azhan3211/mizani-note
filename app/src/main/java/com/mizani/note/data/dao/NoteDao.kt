package com.mizani.note.data.dao

import androidx.room.*
import com.mizani.note.data.entities.NoteEntity
import com.mizani.note.utils.DateFormatter.toEndDay
import com.mizani.note.utils.DateFormatter.toStartDay
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteEntity): Long

    @Query("SELECT * FROM note_table WHERE date BETWEEN :startDate AND :endDate OR (is_repeated = 'true' AND is_reminder_set = 'true' AND date <= :endDate)")
    fun getAll(
        startDate: Date = Calendar.getInstance().time.toStartDay(),
        endDate: Date= Calendar.getInstance().time.toEndDay()
    ): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note_table WHERE (date BETWEEN :startDate AND " +
            ":endDate OR (is_repeated = 'true' AND is_reminder_set = 'true' AND date <= :endDate)) AND" +
            " category = :categoryId")
    fun getAllByCategory(
        categoryId: Long,
        startDate: Date = Calendar.getInstance().time.toStartDay(),
        endDate: Date= Calendar.getInstance().time.toEndDay()
    ): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note_table WHERE (date >= :date OR is_repeated = 'true') AND is_reminder_set = 'true'")
    fun getAllActive(date: Date): List<NoteEntity>

    @Query("DELETE FROM note_table WHERE id=:id")
    fun delete(id: Long)

    @Query("DELETE FROM note_table WHERE category=:categoryId")
    fun deleteByCategory(categoryId: Long)

    @Update
    fun update(note: NoteEntity)

    @Query("SELECT * FROM note_table WHERE id=:id")
    fun get(id: Long): NoteEntity

}