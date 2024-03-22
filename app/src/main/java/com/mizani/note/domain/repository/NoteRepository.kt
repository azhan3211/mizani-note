package com.mizani.note.domain.repository

import com.mizani.note.data.dto.NoteDto
import kotlinx.coroutines.flow.Flow
import java.util.*

interface NoteRepository {
    fun getAllNote(date: Date = Calendar.getInstance().time): Flow<List<NoteDto>>
    fun getAllByCategory(categoryId: Long, date: Date = Calendar.getInstance().time): Flow<List<NoteDto>>
    fun getAllActive(date: Date): List<NoteDto>
    fun insert(note: NoteDto): Long
    fun update(note: NoteDto)
    fun getNote(id: Long): NoteDto
    fun delete(id: Long)
    fun deleteByCategory(categoryId: Long)
}