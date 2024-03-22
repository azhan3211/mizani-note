package com.mizani.note.presentation.fragments.note

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mizani.note.R
import com.mizani.note.data.dto.NoteByCategoryDto
import com.mizani.note.presentation.NoteActivity
import com.mizani.note.presentation.adapter.NoteSectionAdapter
import com.mizani.note.presentation.dialog.DateTimeDialog
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.DateFormatter
import com.mizani.note.utils.DateFormatter.convertToReadable
import com.mizani.note.utils.ViewExt.visibleIf
import kotlinx.android.synthetic.main.fragment_note.note_add_category_textview
import kotlinx.android.synthetic.main.fragment_note.note_add_fab
import kotlinx.android.synthetic.main.fragment_note.note_calendar_imageview
import kotlinx.android.synthetic.main.fragment_note.note_content_linearlayout
import kotlinx.android.synthetic.main.fragment_note.note_date_textview
import kotlinx.android.synthetic.main.fragment_note.note_empty_view
import kotlinx.android.synthetic.main.fragment_note.note_recyclerview
import kotlinx.android.synthetic.main.view_search.view_search_edittext
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
        when (noteSection) {
            is NoteSection.Note -> (activity as? NoteActivity)?.gotoDetailNote(noteSection.note.id.orZero())
            is NoteSection.ViewMore -> (activity as? NoteActivity)?.gotoMoreList(
                noteSection.categoryDto,
                viewModel.getCurrentDate()
            )
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
                viewModel.getNote(date)
            }.show()
        }
        note_add_category_textview.setOnClickListener {
            (activity as? NoteActivity)?.gotoCategory()
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
        viewModel.getNote()
        viewModel.noteData.observe(viewLifecycleOwner) {
            setupView(it)
            setupCurrentDateLabel()
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