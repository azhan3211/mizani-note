package com.mizani.note.presentation.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mizani.note.R
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.presentation.adapter.NoteColorAdapter
import com.mizani.note.presentation.base.FragmentBase
import com.mizani.note.presentation.dialog.DateTimeDialog
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.alarm.AlarmUtil
import com.mizani.note.utils.DateFormatter.convertToReadable
import com.mizani.note.utils.InputValidation.throwWhenEmpty
import com.mizani.note.utils.ViewExt.disableIf
import com.mizani.note.utils.ViewExt.enableIf
import com.mizani.note.utils.ViewExt.gone
import com.mizani.note.utils.ViewExt.visible
import com.mizani.note.utils.ViewExt.visibleIf
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
class NoteAddFragment : FragmentBase() {

    private val viewModel: NoteAddViewModel by inject()

    private val colorAdapter by lazy {
        NoteColorAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickHandler()
        viewModelObserver()
        setupColorAdapter()
    }

    private fun setupColorAdapter() {
        note_add_recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        note_add_recyclerview.adapter = colorAdapter
    }

    private fun clickHandler() {
        note_add_date_edittext.setOnClickListener {
            DateTimeDialog.datePickerDialog(requireContext()) { year, month, day ->
                viewModel.setDate(year, month, day)
            }.show()
        }
        note_add_time_edittext.setOnClickListener {
            DateTimeDialog.timePickerDialog(requireContext()) { hour, minute ->
                viewModel.setTime(hour, minute)
            }.show()
        }
        note_add_cancel_button.setOnClickListener {
            closeFragment()
        }
        note_add_save_button.setOnClickListener {
            try {
                note_add_title_edittext.throwWhenEmpty()
                note_add_description_edittext.throwWhenEmpty()

                val isNewCategoryEditTextVisible = note_add_category_edittext.isVisible
                if (isNewCategoryEditTextVisible) {
                    note_add_category_edittext.throwWhenEmpty()
                    viewModel.setNewCategory(note_add_category_edittext.text.toString())
                } else {
                    if (viewModel.isCategoryNotSelected()) {
                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.setNewCategory("")
                }

                val note = getNoteData()
                viewModel.insert(note)
            } catch (e: Exception) {

            }
        }
        note_add_back_imageview.setOnClickListener {
            closeFragment()
        }
        note_add_switchcompat.setOnCheckedChangeListener { _, isChecked ->
            note_add_reminder_linearlayout.visibleIf(isChecked)
            viewModel.setReminder(isChecked)
        }
        note_add_repeated_switchcompat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRepeated(isChecked)
            note_add_date_edittext.disableIf(isChecked)
        }
        note_add_category_imageview.setOnClickListener {
            val isVisible = note_add_category_edittext.isVisible
            note_add_category_edittext.visibleIf(isVisible.not())
            changeCategoryImageStateView(isVisible.not())
            note_add_category_spinner.enableIf(isVisible)
        }
    }

    private fun changeCategoryImageStateView(isAdd: Boolean) {
        val image = if (isAdd) R.drawable.ic_cancel else R.drawable.ic_add
        val background = if (isAdd) R.drawable.rounded_red else R.drawable.rounded_green
        Glide.with(requireContext()).load(image).into(note_add_category_imageview)
        note_add_category_imageview.background = resources.getDrawable(background, null)
    }

    private fun setupSpinner(categories: List<CategoryDto>) {
        if (categories.isEmpty()) {
            note_add_category_linearlayout.gone()
            note_add_category_edittext.visible()
            return
        }
        val items = arrayListOf("Select Category")
        items.addAll(categories.map { it.name })
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        note_add_category_spinner.adapter = adapter

        note_add_category_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    viewModel.setSelectedCategory(0L)
                } else {
                    viewModel.setSelectedCategory(categories[position - 1].id.orZero())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun setAlarm(note: NoteDto, isCancel: Boolean = false) {
        val intent = AlarmUtil.getIntentNote(requireContext(), note)
        val pendingIntent = AlarmUtil.getPendingIntent(requireContext(), intent, note.id?.toInt().orZero())
        if (isCancel) {
            AlarmUtil.cancelAlarm(requireContext(), pendingIntent)
        } else {
            AlarmUtil.setAlarm(requireContext(), pendingIntent, note.date, note.isRepeated)
        }
    }

    private fun getNoteData(id: Long = 0) = viewModel.generateNote(
        id = id,
        title = note_add_title_edittext.text.toString(),
        description = note_add_description_edittext.text.toString(),
        color = colorAdapter.getSelectedColor()
    )

    private fun viewModelObserver() {
        viewModel.observeCurrentDateTime().observe(viewLifecycleOwner) {
            note_add_date_edittext.setText(it.time.convertToReadable("dd MMM yyyy"))
            note_add_time_edittext.setText(it.time.convertToReadable("HH:mm"))
        }
        viewModel.observeNewNote().observe(viewLifecycleOwner) {
            if (it.isReminderSet) {
                setAlarm(it)
            }
            closeFragment()
        }
        lifecycleScope.launch {
            viewModel.getCategory().collect {
                setupSpinner(it)
            }
        }
    }

    companion object {

        fun getInstance(): Fragment {
            return NoteAddFragment()
        }

    }

}