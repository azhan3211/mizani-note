package com.mizani.note.domain.mapper

import com.mizani.note.data.entities.NoteEntity
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.utils.DataTypeExt.orZero

object NoteMapper {

    fun mapEntityToDto(note: NoteEntity) = NoteDto(
        note.id,
        note.title,
        note.description,
        note.date,
        note.color,
        note.isRepeated,
        note.isReminderSet,
        note.category
    )

    fun mapDtoToEntity(note: NoteDto) = NoteEntity(
        id = note.id.orZero(),
        title = note.title,
        description = note.description,
        date = note.date,
        color = note.color.orEmpty(),
        isRepeated = note.isRepeated,
        isReminderSet = note.isReminderSet,
        category = note.category
    )

}