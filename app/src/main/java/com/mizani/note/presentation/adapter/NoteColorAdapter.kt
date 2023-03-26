package com.mizani.note.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mizani.note.R
import com.mizani.note.utils.NoteThemeConfig
import kotlinx.android.synthetic.main.row_colors.view.*

class NoteColorAdapter() : RecyclerView.Adapter<NoteColorAdapter.ViewHolder>() {

    private var selectedIndex = 0

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(color: String, index: Int) {
            view.row_note_color_cardview.setCardBackgroundColor(Color.parseColor(color))
            view.row_note_color_imageview.visibility = if (selectedIndex == index) {
                View.VISIBLE
            } else {
                View.GONE
            }
            view.setOnClickListener {
                if (selectedIndex == index) return@setOnClickListener
                notifyItemChanged(selectedIndex)
                selectedIndex = index
                notifyItemChanged(selectedIndex)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.row_colors, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(NoteThemeConfig.colors[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = NoteThemeConfig.colors.size

    fun setSelectedColor(color: String) {
        notifyItemChanged(selectedIndex)
        selectedIndex = NoteThemeConfig.colors.indexOf(color)
        notifyItemChanged(selectedIndex)
    }

    fun getSelectedColor(): String {
        return NoteThemeConfig.colors[selectedIndex]
    }

}