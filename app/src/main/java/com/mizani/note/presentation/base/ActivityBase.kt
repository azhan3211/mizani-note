package com.mizani.note.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mizani.note.R

open class ActivityBase : AppCompatActivity() {

    private var fragmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentId = R.id.activity_frame_layout
    }

    fun initFirstFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(fragmentId, fragment)
            .commit()
    }

    fun changeFragment(fragment: Fragment, isAddToBackStack: Boolean = false) {
        val fm = supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        if (isAddToBackStack) {
            fm.add(fragmentId, fragment).addToBackStack(fragment.tag)
        } else {
            fm.replace(fragmentId, fragment)
        }

        fm.commit()
    }

}