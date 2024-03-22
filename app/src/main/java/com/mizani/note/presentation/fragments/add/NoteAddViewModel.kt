package com.mizani.note.presentation.fragments.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.domain.repository.CategoryRepository
import com.mizani.note.domain.repository.NoteRepository
import com.mizani.note.utils.DataTypeExt.orFalse
import com.mizani.note.utils.DataTypeExt.orZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NoteAddViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val currentDateTime = MutableLiveData(Calendar.getInstance())
    private val newInsertedNoted = MutableLiveData<NoteDto>()
    private val isReminderSet = MutableLiveData(false)
    private val isRepeatedSet = MutableLiveData(false)
    private var newCategory = ""
    private var selectedCategory = 0L

    fun insert(note: NoteDto) {
        viewModelScope.launch(Dispatchers.IO) {
            var categoryId = getCategoryId(note)
            val noteModified = note.copy(category = categoryId)
            val id = noteRepository.insert(noteModified)
            val newNote = noteRepository.getNote(id)
            launch(Dispatchers.Main) {
                newInsertedNoted.value = newNote
            }
        }
    }

    fun setSelectedCategory(categoryId: Long) {
        selectedCategory = categoryId
    }

    fun isCategoryNotSelected() = selectedCategory == 0L

    fun getCategory() = categoryRepository.getAll()

    private fun getCategoryId(noteDto: NoteDto): Long {
        var categoryId = noteDto.category
        if (newCategory.isNotEmpty()) {
            categoryId = categoryRepository.insert(
                CategoryDto(
                name = newCategory
            ))
        }
        return categoryId.orZero()
    }

    fun setDate(year: Int, month: Int, date: Int) {
        val current = currentDateTime.value
        current?.set(Calendar.YEAR, year)
        current?.set(Calendar.MONTH, month)
        current?.set(Calendar.DAY_OF_MONTH, date)
        currentDateTime.value = current ?: Calendar.getInstance()
    }

    fun setTime(hour: Int, minute: Int) {
        val current = currentDateTime.value
        current?.set(Calendar.HOUR_OF_DAY, hour)
        current?.set(Calendar.MINUTE, minute)
        currentDateTime.value = current ?: Calendar.getInstance()
    }

    fun setReminder(isReminderSet: Boolean) {
        this.isReminderSet.value = isReminderSet
    }

    fun setRepeated(isRepeated: Boolean) {
        this.isRepeatedSet.value = isRepeated
    }

    fun setNewCategory(isNew: String) {
        newCategory = isNew
    }

    fun observeCurrentDateTime(): LiveData<Calendar> = currentDateTime

    fun observeNewNote(): LiveData<NoteDto> = newInsertedNoted

    fun generateNote(
        id: Long = 0,
        title: String,
        description: String,
        color: String
    ) = NoteDto(
        id = id,
        title = title,
        description = description,
        date = currentDateTime.value?.time ?: Calendar.getInstance().time,
        color = color,
        isRepeated = isRepeatedSet.value.orFalse(),
        isReminderSet = isReminderSet.value.orFalse(),
        category = selectedCategory
    )

}