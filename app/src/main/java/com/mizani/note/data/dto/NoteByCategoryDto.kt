package com.mizani.note.data.dto

data class NoteByCategoryDto(
    val category: CategoryDto,
    val notes: List<NoteDto>
)