package com.mizani.note.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mizani.note.R
import com.mizani.note.data.dto.NoteByCategoryDto
import com.mizani.note.presentation.fragments.note.NoteSection
import kotlinx.android.synthetic.main.row_note_section.view.*

class NoteSectionAdapter(
    private val action: (noteSection: NoteSection) -> Unit
) : RecyclerView.Adapter<NoteSectionAdapter.ViewHolder>() {

    private val data = arrayListOf<NoteByCategoryDto>()
    private val dataModified = arrayListOf<NoteByCategoryDto>()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val adapter by lazy {
            NoteAdapter {
                action.invoke(NoteSection.Note(it))
            }
        }

        fun bind(noteByCategoryDto: NoteByCategoryDto) {
            view.row_note_section_more_textview.setOnClickListener {
                action.invoke(NoteSection.ViewMore(noteByCategoryDto.category))
            }
            view.row_note_section_textview.text = noteByCategoryDto.category.name
            view.row_note_section_recyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            view.row_note_section_recyclerview.adapter = adapter
            adapter.setData(noteByCategoryDto.notes.take(5))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_note_section, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataModified[holder.adapterPosition])
    }

    override fun getItemCount(): Int = dataModified.size

    fun setData(data: List<NoteByCategoryDto>) {
        this.data.clear()
        this.data.addAll(data)
        this.dataModified.clear()
        this.dataModified.addAll(data)
        notifyDataSetChanged()
    }

    fun filterData(keyword: String) {
        this.dataModified.clear()
        if (keyword.isEmpty()) {
            this.dataModified.addAll(data)
        } else {
            val searchData = arrayListOf<NoteByCategoryDto>()
            this.data.forEach {
                val filteredNotes = it.notes.filter { note ->
                    note.title.contains(keyword) || note.description.contains(keyword)
                }
                if (filteredNotes.isNotEmpty()) {
                    val data = it.copy(notes = filteredNotes)
                    searchData.add(data)
                }
            }
            this.dataModified.addAll(searchData)
        }
        notifyDataSetChanged()
    }
}