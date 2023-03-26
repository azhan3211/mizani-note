package com.mizani.note.presentation.base

import androidx.fragment.app.Fragment

open class FragmentBase : Fragment() {

    fun closeFragment() {
        activity?.supportFragmentManager?.popBackStack()
    }

}