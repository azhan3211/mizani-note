package com.mizani.note.presentation.fragments.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mizani.note.R
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.presentation.adapter.CategoryAdapter
import com.mizani.note.presentation.base.FragmentBase
import com.mizani.note.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_category.category_imageview_back
import kotlinx.android.synthetic.main.fragment_category.category_recyclerview
import kotlinx.android.synthetic.main.view_search.view_search_edittext
import org.koin.android.ext.android.inject

class CategoryFragment : FragmentBase() {

    private val viewModel: CategoryViewModel by inject()

    private val categoryAdapter by lazy {
        CategoryAdapter(::onDelete)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setupData()
        listenerHandler()
    }

    private fun setupData() {
        category_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        category_recyclerview.adapter = categoryAdapter
    }

    private fun observer() {
        viewModel.getCategory()
        viewModel.category.observe(viewLifecycleOwner) {
            categoryAdapter.setData(it)
        }
        viewModel.categorySearch.observe(viewLifecycleOwner) {
            categoryAdapter.setData(it)
        }
    }

    private fun listenerHandler() {
        category_imageview_back.setOnClickListener {
            closeFragment()
        }
        view_search_edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun onDelete(category: CategoryDto) {
        DialogUtils.showConfirmationDialog(
            requireContext(),
            description = getString(R.string.delete_category_description),
            title = getString(R.string.delete_category),
            negativeAction = {

            },
            positiveAction = {
                viewModel.delete(category)
            }
        )
    }

    companion object {
        fun getInstance(): CategoryFragment {
            return CategoryFragment()
        }
    }

}