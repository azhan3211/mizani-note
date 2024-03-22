package com.mizani.note.presentation.fragments.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mizani.note.R
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.presentation.adapter.NoteColorAdapter
import com.mizani.note.presentation.base.FragmentBase
import com.mizani.note.presentation.dialog.DateTimeDialog
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.DateFormatter.convertToReadable
import com.mizani.note.utils.DialogUtils
import com.mizani.note.utils.InputValidation.throwWhenEmpty
import com.mizani.note.utils.ViewExt.disableIf
import com.mizani.note.utils.ViewExt.enableIf
import com.mizani.note.utils.ViewExt.gone
import com.mizani.note.utils.ViewExt.goneIf
import com.mizani.note.utils.ViewExt.visible
import com.mizani.note.utils.ViewExt.visibleIf
import com.mizani.note.utils.alarm.AlarmUtil
import kotlinx.android.synthetic.main.fragment_add_note.*
import org.koin.android.ext.android.inject
import java.util.*

class NoteUpdateFragment : FragmentBase() {

    private val viewModel: NoteUpdateViewModel by inject()

    private val noteId by lazy {
        arguments?.getLong(NOTE_ID, 0L) ?: 0L
    }

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
        setupColorAdapter()
        initListener()
        viewModelObserver()
    }

    private fun setupData(note: NoteDto) {
        viewModel.setNoteDate()
        note_add_description_edittext.setText(note.description)
        note_add_title_edittext.setText(note.title)
        colorAdapter.setSelectedColor(note.color ?: "")
        note_add_switchcompat.isChecked = note.isReminderSet
        note_add_reminder_linearlayout.visibleIf(note.isReminderSet)
        note_add_repeated_switchcompat.isChecked = note.isRepeated
        note_add_delete_imageview.visible()
        note_add_category_imageview.gone()
        note_add_category_spinner.isEnabled = false
    }

    private fun setupColorAdapter() {
        note_add_recyclerview.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        note_add_recyclerview.adapter = colorAdapter
    }

    private fun initListener() {
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
            if (viewModel.isUpdateEnabled()) {
                viewModel.setUpdateEnabled(false)
            } else {
                closeFragment()
            }
        }
        note_add_save_button.setOnClickListener {
            try {
                if (viewModel.isUpdateEnabled().not()) {
                    viewModel.setUpdateEnabled(true)
                    return@setOnClickListener
                }

                note_add_title_edittext.throwWhenEmpty()
                note_add_description_edittext.throwWhenEmpty()

                val isNewCategoryEditTextVisible = note_add_category_edittext.isVisible
                if (isNewCategoryEditTextVisible) {
                    note_add_category_edittext.throwWhenEmpty()
                    viewModel.setNewCategory(note_add_category_edittext.text.toString())
                } else {
                    if (viewModel.isCategoryNotSelected()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_select_category),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    viewModel.setNewCategory("")
                }

                viewModel.update(
                    title = note_add_title_edittext.text.toString(),
                    description = note_add_description_edittext.text.toString(),
                    color = colorAdapter.getSelectedColor(),
                    isReminderSet = note_add_switchcompat.isChecked,
                    isRepeated = note_add_repeated_switchcompat.isChecked
                )
            } catch (_: Exception) {

            }
        }
        note_add_back_imageview.setOnClickListener {
            closeFragment()
        }
        note_add_switchcompat.setOnCheckedChangeListener { _, isChecked ->
            note_add_reminder_linearlayout.visibleIf(isChecked)
        }
        note_add_repeated_switchcompat.setOnCheckedChangeListener { _, isChecked ->
            note_add_date_edittext.disableIf(isChecked)
        }
        note_add_delete_imageview.setOnClickListener {
            DialogUtils.showConfirmationDialog(
                context = requireContext(),
                title = getString(R.string.delete_note_title),
                positiveAction = {
                    viewModel.cancelAlarm()
                    viewModel.delete()
                    closeFragment()
                },
                negativeAction = {

                }
            )
        }
        note_add_category_imageview.setOnClickListener {
            val isVisible = note_add_category_edittext.isVisible
            note_add_category_edittext.visibleIf(isVisible.not())
            changeCategoryImageStateView(isVisible.not())
            note_add_category_spinner.enableIf(isVisible)
        }
        note_add_category_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setSelectedCategory(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun changeCategoryImageStateView(isAdd: Boolean) {
        val image = if (isAdd) R.drawable.ic_cancel else R.drawable.ic_add
        val background = if (isAdd) R.drawable.rounded_red else R.drawable.rounded_green
        Glide.with(requireContext()).load(image).into(note_add_category_imageview)
        note_add_category_imageview.background =
            ResourcesCompat.getDrawable(resources, background, null)
    }

    private fun setAlarm(note: NoteDto, isCancel: Boolean = false) {
        val intent = AlarmUtil.getIntentNote(requireContext(), note)
        val pendingIntent =
            AlarmUtil.getPendingIntent(requireContext(), intent, note.id?.toInt().orZero())
        if (isCancel) {
            AlarmUtil.cancelAlarm(requireContext(), pendingIntent)
        } else {
            AlarmUtil.setAlarm(requireContext(), pendingIntent, note.date, note.isRepeated)
        }
    }

    private fun setupSpinner(categories: List<CategoryDto>) {
        if (categories.isEmpty()) {
            note_add_category_linearlayout.gone()
            note_add_category_edittext.visible()
            return
        }
        val items = arrayListOf(getString(R.string.select_category))
        items.addAll(categories.map { it.name })
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        note_add_category_spinner.adapter = adapter
        viewModel.setSelectedCategorySpinner()
    }

    private fun setupUpdateMode(isUpdateEnabled: Boolean) {
        note_add_save_button.text = if (isUpdateEnabled) "Save" else "Update"
        val isDisabled = isUpdateEnabled.not()
        note_add_date_edittext.disableIf(isDisabled)
        note_add_time_edittext.disableIf(isDisabled)
        note_add_title_edittext.disableIf(isDisabled)
        note_add_description_edittext.disableIf(isDisabled)
        note_add_switchcompat.disableIf(isDisabled)
        note_add_repeated_switchcompat.disableIf(isDisabled)
        note_add_recyclerview.goneIf(isDisabled)
        note_add_category_imageview.visibleIf(isUpdateEnabled)
        note_add_category_spinner.enableIf(isUpdateEnabled)
    }

    private fun viewModelObserver() {
        viewModel.getNote(noteId)
        viewModel.note.observe(viewLifecycleOwner) {
            it?.let { note ->
                setupData(note)
                viewModel.getCategory()
            }
        }
        viewModel.selectedCategorySpinner.observe(viewLifecycleOwner) {
            note_add_category_spinner.setSelection(it)
        }
        viewModel.cancelAlarm.observe(viewLifecycleOwner) {
            it?.let {
                setAlarm(it, true)
            }
        }
        viewModel.observeCurrentDateTime().observe(viewLifecycleOwner) {
            note_add_date_edittext.setText(it.time.convertToReadable("dd MMM yyyy"))
            note_add_time_edittext.setText(it.time.convertToReadable("HH:mm"))
        }
        viewModel.updateEnabled.observe(viewLifecycleOwner) {
            setupUpdateMode(it)
        }
        viewModel.observeIsUpdated().observe(viewLifecycleOwner) {
            // cancel alarm for previous note
            if (it.isReminderSet) {
                setAlarm(it, true)
            }

            if (it.isReminderSet) {
                setAlarm(it)
            }
            closeFragment()
        }
        viewModel.category.observe(viewLifecycleOwner) {
            setupSpinner(it)
        }
    }

    companion object {
        private const val NOTE_ID = "NOTE_ID"

        fun getInstance(noteId: Long): NoteUpdateFragment {
            val fragment = NoteUpdateFragment()
            fragment.arguments = bundleOf(
                NOTE_ID to noteId
            )
            return fragment
        }
    }

}