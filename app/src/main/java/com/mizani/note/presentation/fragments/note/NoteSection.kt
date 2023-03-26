package com.mizani.note.presentation.fragments.note

import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto

sealed class NoteSection {

    data class Note(val note: NoteDto): NoteSection()
    data class ViewMore(val categoryDto: CategoryDto): NoteSection()

}