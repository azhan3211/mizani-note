package com.mizani.note.presentation

import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import java.util.Date

interface NoteInterface {

    fun gotoAddNote()
    fun gotoDetailNote(id: Long)
    fun gotoMoreList(categoryDto: CategoryDto, date: Date)

    fun gotoCategory()

}