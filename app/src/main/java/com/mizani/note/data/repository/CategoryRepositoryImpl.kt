package com.mizani.note.data.repository

import com.mizani.note.data.dao.CategoryDao
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.domain.mapper.CategoryMapper
import com.mizani.note.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.flow

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
): CategoryRepository {

    override fun getAll() = flow {
        categoryDao.getAll().collect {
            val data = it.map { category -> CategoryMapper.mapEntityToDto(category) }
            emit(data)
        }
    }

    override fun insert(categoryDto: CategoryDto) =
        categoryDao.insert(CategoryMapper.mapDtoToEntity(categoryDto))

    override fun delete(id: Long) {
        categoryDao.delete(id)
    }

}