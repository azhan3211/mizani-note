package com.mizani.note.data.repository

import com.mizani.note.data.dao.NoteDao
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.domain.mapper.NoteMapper
import com.mizani.note.domain.repository.NoteRepository
import com.mizani.note.utils.DateFormatter.toEndDay
import com.mizani.note.utils.DateFormatter.toStartDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class NoteRepositoryImpl(private val noteDao: NoteDao): NoteRepository {

    override fun getAllNote(date: Date): Flow<List<NoteDto>> {
        return flow {
            val startDate = date.toStartDay()
            val endDate = date.toEndDay()
            noteDao.getAll(startDate, endDate).collect {
                val data = it.map { note ->
                    NoteMapper.mapEntityToDto(note)
                }
                emit(data)
            }
        }
    }

    override fun getAllByCategory(categoryId: Long, date: Date): Flow<List<NoteDto>> {
        return flow {
            val startDate = date.toStartDay()
            val endDate = date.toEndDay()
            noteDao.getAllByCategory(categoryId, startDate, endDate).collect {
                val data = it.map { note ->
                    NoteMapper.mapEntityToDto(note)
                }
                emit(data)
            }
        }
    }

    override fun getAllActive(date: Date) = noteDao.getAllActive(date).map {
        NoteMapper.mapEntityToDto(it)
    }

    override fun insert(note: NoteDto) = noteDao.insert(NoteMapper.mapDtoToEntity(note))

    override fun update(note: NoteDto) {
        noteDao.update(NoteMapper.mapDtoToEntity(note))
    }

    override fun getNote(id: Long) = NoteMapper.mapEntityToDto(noteDao.get(id))

    override fun delete(id: Long) {
        noteDao.delete(id)
    }

}