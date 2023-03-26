package com.mizani.note.presentation.fragments.detaillist

import androidx.lifecycle.ViewModel
import com.mizani.note.domain.repository.NoteRepository
import java.util.*

class DetailListViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    fun getNotes(categoryId: Long, date: Date) = noteRepository.getAllByCategory(categoryId, date)

}