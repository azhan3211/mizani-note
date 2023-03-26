package com.mizani.note.domain.repository

import com.mizani.note.data.dao.CategoryDao
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.domain.mapper.CategoryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CategoryRepository {

    fun getAll(): Flow<List<CategoryDto>>
    fun insert(categoryDto: CategoryDto): Long
    fun delete(id: Long)

}