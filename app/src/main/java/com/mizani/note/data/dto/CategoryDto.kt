package com.mizani.note.data.dto

import java.io.Serializable

data class CategoryDto (
    val id: Long? = 0,
    val name: String = ""
) : Serializable