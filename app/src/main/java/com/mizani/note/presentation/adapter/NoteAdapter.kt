package com.mizani.note.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mizani.note.R
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.utils.DateFormatter.convertToReadable
import kotlinx.android.synthetic.main.row_note.view.*

class NoteAdapter(
    private val action: (note: NoteDto) -> Unit
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val data = arrayListOf<NoteDto>()
    private val dataModified = arrayListOf<NoteDto>()

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(note: NoteDto) {
            val pattern = if (note.isRepeated) "HH:mm" else "EEEE, dd MMM yyyy HH:mm"
            val day = if (note.isRepeated) "Everyday, " else ""
            view.note_row_title_textview.text = note.title
            view.note_row_description_textview.text = note.description
            view.note_row_date_textview.text = day + note.date?.convertToReadable(pattern)
            view.row_note_color_cardview.setCardBackgroundColor(Color.parseColor(note.color))
            view.setOnClickListener {
                action.invoke(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataModified[holder.adapterPosition])
    }

    override fun getItemCount() = dataModified.size

    fun setData(data: List<NoteDto>) {
        this.data.clear()
        this.dataModified.clear()
        this.data.addAll(data)
        this.dataModified.addAll(data)
        notifyDataSetChanged()
    }

    fun filterData(keyword: String) {
        this.dataModified.clear()
        if (keyword.isEmpty()) {
            this.dataModified.addAll(data)
        } else {
            val searchData = this.data.filter { note ->
                note.title.lowercase().contains(keyword.lowercase()) || note.description.lowercase().contains(keyword.lowercase())
            }
            if (searchData.isNotEmpty()) {
                this.dataModified.addAll(searchData)
            }
        }
        notifyDataSetChanged()
    }

}