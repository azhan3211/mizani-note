package com.mizani.note.presentation.fragments.detaillist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mizani.note.R
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.presentation.NoteActivity
import com.mizani.note.presentation.base.FragmentBase
import com.mizani.note.presentation.dialog.DateTimeDialog
import com.mizani.note.presentation.adapter.NoteAdapter
import com.mizani.note.utils.BundleExt.getSerializableData
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.DateFormatter
import com.mizani.note.utils.DateFormatter.convertToReadable
import com.mizani.note.utils.ViewExt.visibleIf
import kotlinx.android.synthetic.main.fragment_detail_list.*
import kotlinx.android.synthetic.main.view_search.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*

class NoteDetailListFragment : FragmentBase() {

    private val viewModel: DetailListViewModel by inject()

    private val categoryDto by lazy {
        arguments?.getSerializableData(CATEGORY_DTO, CategoryDto::class.java)?: CategoryDto()
    }

    private val date by lazy {
        arguments?.getSerializableData(DATE, Date::class.java)?:Date()
    }

    private val adapter by lazy {
        NoteAdapter {
            (activity as? NoteActivity)?.gotoDetailNote(it.id.orZero())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        viewModelObserver()
        eventHandler()
    }

    private fun setupCurrentDateLabel() {
        val today = date.convertToReadable("dd MMM yyyy")
        note_detail_list_date_textview.text = "Today, $today"
    }


    private fun eventHandler() {
        note_detail_list_back_imageview.setOnClickListener {
            closeFragment()
        }
        note_detail_list_calendar_imageview.setOnClickListener {
            DateTimeDialog.datePickerDialog(requireContext()) { year: Int, month: Int, day: Int ->
                val date = DateFormatter.setDate(year, month, day)
                note_detail_list_date_textview.text = date.convertToReadable("EEE, dd MMM yyyy")
                lifecycleScope.launch {
                    setupView(viewModel.getNotes(categoryDto.id.orZero(), date).first())
                }
            }.show()
        }

        view_search_edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filterData(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setupList() {
        note_detail_list_recyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        note_detail_list_recyclerview.adapter = adapter
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            viewModel.getNotes(categoryDto.id.orZero(), date).collect {
                setupView(it)
                setupCurrentDateLabel()
            }
        }
    }

    private fun setupView(data: List<NoteDto>) {
        adapter.setData(data)
        note_detail_list_empty_view.visibleIf(data.isEmpty())
        note_detail_list_content_linearlayout.visibleIf(data.isNotEmpty())
    }

    companion object {

        private const val CATEGORY_DTO = "CATEGORY_ID"
        private const val DATE = "DATE"

        fun getInstance(categoryDto: CategoryDto, date: Date): NoteDetailListFragment {
            return NoteDetailListFragment().apply {
                this.arguments = bundleOf(
                    CATEGORY_DTO to categoryDto,
                    DATE to date
                )
            }
        }
    }

}