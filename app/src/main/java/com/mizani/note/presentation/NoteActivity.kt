package com.mizani.note.presentation

import android.os.Bundle
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.presentation.base.ActivityBase
import com.mizani.note.presentation.fragments.add.NoteAddFragment
import com.mizani.note.presentation.fragments.detaillist.NoteDetailListFragment
import com.mizani.note.presentation.fragments.note.NoteFragment
import com.mizani.note.presentation.fragments.update.NoteUpdateFragment
import com.mizani.note.utils.NotificationUtils
import java.util.*

class NoteActivity : ActivityBase(), NoteInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirstFragment(NoteFragment.getInstance())
        NotificationUtils.createNotificationChannel(this)
    }

    override fun gotoAddNote() {
        changeFragment(NoteAddFragment.getInstance(), true)
    }

    override fun gotoDetailNote(note: NoteDto) {
        changeFragment(NoteUpdateFragment.getInstance(note), true)
    }

    override fun gotoMoreList(categoryDto: CategoryDto, date: Date) {
        changeFragment(NoteDetailListFragment.getInstance(categoryDto, date), true)
    }


}