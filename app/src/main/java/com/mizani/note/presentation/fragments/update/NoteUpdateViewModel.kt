package com.mizani.note.presentation.fragments.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.domain.repository.CategoryRepository
import com.mizani.note.domain.repository.NoteRepository
import com.mizani.note.utils.DataTypeExt.orZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class NoteUpdateViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val isUpdated = MutableLiveData<NoteDto>()
    private val _updateEnabled = MutableLiveData(false)
    val updateEnabled: LiveData<Boolean> = _updateEnabled
    private val currentDateTime = MutableLiveData(Calendar.getInstance())
    private val _cancelAlarmEvent = MutableLiveData<NoteDto>(null)
    val cancelAlarm: LiveData<NoteDto> = _cancelAlarmEvent
    private val _note = MutableLiveData<NoteDto>(null)
    val note: LiveData<NoteDto> = _note
    private val _category = MutableLiveData<List<CategoryDto>>()
    val category: LiveData<List<CategoryDto>> = _category
    private val _selectedCategorySpinner = MutableLiveData<Int>()
    val selectedCategorySpinner: LiveData<Int> = _selectedCategorySpinner
    private var newCategory = ""
    private var selectedCategoryId = 0L

    fun getNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteRepository.getNote(id)
            launch(Dispatchers.Main) {
                _note.value = note
            }
        }
    }

    fun update(
        title: String,
        description: String,
        color: String,
        isRepeated: Boolean,
        isReminderSet: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            note.value?.let {
                val categoryId = getCategoryId(it)
                val noteModified = it.copy(
                    title = title,
                    description = description,
                    color = color,
                    category = categoryId,
                    isRepeated = isRepeated,
                    isReminderSet = isReminderSet,
                    date = currentDateTime.value?.time ?: Calendar.getInstance().time
                )
                noteRepository.update(noteModified)
                val noteUpdate = noteRepository.getNote(it.id.orZero())
                launch(Dispatchers.Main) {
                    isUpdated.value = noteUpdate
                }
            }
        }
    }

    private fun getCategoryId(noteDto: NoteDto): Long {
        var categoryId = noteDto.category
        if (newCategory.isNotEmpty()) {
            categoryId = categoryRepository.insert(
                CategoryDto(
                    name = newCategory
                )
            )
        }
        return categoryId.orZero()
    }

    fun setNoteDate() {
        note.value?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it.date
            val current = currentDateTime.value
            current?.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            current?.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            current?.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            current?.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            current?.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            currentDateTime.value = current ?: Calendar.getInstance()
        }
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

    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.delete(note.value?.id.orZero())
        }
    }

    fun cancelAlarm() {
        if (note.value?.isReminderSet == true) {
            _cancelAlarmEvent.value = note.value
        }
    }

    fun setNewCategory(isNew: String) {
        this.newCategory = isNew
    }

    fun setUpdateEnabled(isEnabled: Boolean) {
        _updateEnabled.value = isEnabled
    }

    fun setSelectedCategory(position: Int) {
        if (position == 0) {
            selectedCategoryId = 0L
        } else {
            selectedCategoryId = category.value?.get(position - 1)?.id.orZero()
        }
    }

    fun setSelectedCategorySpinner() {
        _selectedCategorySpinner.value =
            category.value?.indexOf(category.value?.find { it.id == note.value?.category })?.inc()
    }

    fun isCategoryNotSelected() = selectedCategoryId == 0L

    fun getCategory() {
        viewModelScope.launch {
            categoryRepository.getAll().collect {
                _category.value = it
            }
        }
    }

    fun isUpdateEnabled() = _updateEnabled.value ?: false

    fun observeCurrentDateTime(): LiveData<Calendar> = currentDateTime
    fun observeIsUpdated(): LiveData<NoteDto> = isUpdated

}