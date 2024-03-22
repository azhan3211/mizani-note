package com.mizani.note.data.dto

import java.io.Serializable
import java.util.*

data class NoteDto(
    val id: Long? = 0,
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val color: String? = null,
    val isRepeated: Boolean = false,
    val isReminderSet: Boolean = false,
    val category: Long = 0L
): Serializable