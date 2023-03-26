package com.mizani.note.presentation.fragments.note

import androidx.lifecycle.ViewModel
import com.mizani.note.data.dto.NoteByCategoryDto
import com.mizani.note.domain.repository.CategoryRepository
import com.mizani.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var currentDate = Calendar.getInstance().time

    fun getNote(date: Date = Calendar.getInstance().time): Flow<List<NoteByCategoryDto>> {
        return flow {
            noteRepository.getAllNote(date).collect { notes ->
                val data = arrayListOf<NoteByCategoryDto>()
                val categories = categoryRepository.getAll().first()
                categories.forEach { category ->
                    val notesGrouped = notes.filter { note -> note.category == category.id }.take(5)
                    if (notesGrouped.isNotEmpty()) {
                        data.add(
                            NoteByCategoryDto(
                            category,
                            notesGrouped
                        )
                        )
                    }
                }
                emit(data)
            }
        }
    }

    fun setCurrentData(date: Date) {
        currentDate = date
    }

    fun getCurrentDate(): Date = currentDate

}