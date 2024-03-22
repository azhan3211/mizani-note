package com.mizani.note.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mizani.note.R
import com.mizani.note.data.dto.CategoryDto
import kotlinx.android.synthetic.main.row_category.view.row_category_imageview_delete
import kotlinx.android.synthetic.main.row_category.view.row_category_textview_name

class CategoryAdapter(
    private val onDelete: (CategoryDto) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val data = arrayListOf<CategoryDto>()

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: CategoryDto) {
            view.row_category_textview_name.text = category.name
            view.row_category_imageview_delete.setOnClickListener {
                onDelete.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_category, parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[holder.adapterPosition])
    }

    fun setData(categories: List<CategoryDto>) {
        data.clear()
        data.addAll(categories)
        notifyDataSetChanged()
    }
}