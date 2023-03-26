package com.mizani.note.presentation.fragments.note

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mizani.note.R
import com.mizani.note.data.dto.NoteByCategoryDto
import com.mizani.note.presentation.NoteActivity
import com.mizani.note.presentation.adapter.NoteSectionAdapter
import com.mizani.note.presentation.dialog.DateTimeDialog
import com.mizani.note.utils.DateFormatter
import com.mizani.note.utils.DateFormatter.convertToReadable
import com.mizani.note.utils.ViewExt.visibleIf
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.view_search.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Calendar

class NoteFragment : Fragment() {

    private val viewModel: NoteViewModel by inject()

    private val adapter by lazy {
        NoteSectionAdapter(::onNoteClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    private fun onNoteClicked(noteSection: NoteSection) {
        when(noteSection) {
            is NoteSection.Note -> (activity as? NoteActivity)?.gotoDetailNote(noteSection.note)
            is NoteSection.ViewMore -> (activity as? NoteActivity)?.gotoMoreList(noteSection.categoryDto, viewModel.getCurrentDate())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModelObserver()
        eventHandler()
    }

    private fun eventHandler() {
        note_add_fab.setOnClickListener {
            (activity as? NoteActivity)?.gotoAddNote()
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
        note_calendar_imageview.setOnClickListener {
            DateTimeDialog.datePickerDialog(requireContext()) { year: Int, month: Int, day: Int ->
                val date = DateFormatter.setDate(year, month, day)
                viewModel.setCurrentData(date)
                note_date_textview.text = date.convertToReadable("EEE, dd MMM yyyy")
                lifecycleScope.launch {
                    setupView(viewModel.getNote(date).first())
                }
            }.show()
        }
    }

    private fun setupCurrentDateLabel() {
        val today = Calendar.getInstance().time.convertToReadable("dd MMM yyyy")
        note_date_textview.text = "Today, $today"
    }

    private fun setupRecyclerView() {
        note_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        note_recyclerview.adapter = adapter
        note_recyclerview.isNestedScrollingEnabled = false
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            viewModel.getNote().collect {
                setupView(it)
                setupCurrentDateLabel()
            }
        }
    }

    private fun setupView(data: List<NoteByCategoryDto>) {
        adapter.setData(data)
        note_empty_view.visibleIf(data.isEmpty())
        note_content_linearlayout.visibleIf(data.isNotEmpty())
    }

    companion object {

        fun getInstance(): Fragment {
            return NoteFragment()
        }
    }

}