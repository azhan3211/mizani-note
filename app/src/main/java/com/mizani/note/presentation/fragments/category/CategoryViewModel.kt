package com.mizani.note.presentation.fragments.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.domain.repository.CategoryRepository
import com.mizani.note.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _category = MutableLiveData<List<CategoryDto>>()
    val category: LiveData<List<CategoryDto>> = _category
    private val _categorySearch = MutableLiveData<List<CategoryDto>>()
    val categorySearch: LiveData<List<CategoryDto>> = _categorySearch

    fun getCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getAll().collect {
                launch(Dispatchers.Main) {
                    _category.value = it
                }
            }
        }
    }

    fun search(keyword: String) {
        if (keyword.isEmpty()) {
            _categorySearch.value = _category.value
        } else {
            _categorySearch.value = _category.value?.filter {
                it.name.lowercase().contains(keyword.lowercase())
            }
        }
    }

    fun delete(categoryDto: CategoryDto) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.delete(categoryDto.id ?: 0)
            noteRepository.deleteByCategory(categoryDto.id ?: 0)
        }
    }

}