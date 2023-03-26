package com.mizani.note.domain.mapper

import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.entities.NoteCategoryEntity
import com.mizani.note.utils.DataTypeExt.orZero

object CategoryMapper {

    fun mapEntityToDto(categoryEntity: NoteCategoryEntity) = CategoryDto(
        id = categoryEntity.id.orZero(),
        name = categoryEntity.name
    )

    fun mapDtoToEntity(categoryDto: CategoryDto) = NoteCategoryEntity(
        id = categoryDto.id.orZero(),
        name = categoryDto.name
    )

}