package com.mizani.note.presentation

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.mizani.note.data.dto.CategoryDto
import com.mizani.note.presentation.base.ActivityBase
import com.mizani.note.presentation.fragments.add.NoteAddFragment
import com.mizani.note.presentation.fragments.category.CategoryFragment
import com.mizani.note.presentation.fragments.detaillist.NoteDetailListFragment
import com.mizani.note.presentation.fragments.note.NoteFragment
import com.mizani.note.presentation.fragments.update.NoteUpdateFragment
import com.mizani.note.utils.NotificationUtils
import java.util.Date

class NoteActivity : ActivityBase(), NoteInterface {

    private val notificationPostPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPostPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        initFirstFragment(NoteFragment.getInstance())
        NotificationUtils.createNotificationChannel(this)
    }

    override fun gotoAddNote() {
        changeFragment(NoteAddFragment.getInstance(), true)
    }

    override fun gotoDetailNote(id: Long) {
        changeFragment(NoteUpdateFragment.getInstance(id), true)
    }

    override fun gotoMoreList(categoryDto: CategoryDto, date: Date) {
        changeFragment(NoteDetailListFragment.getInstance(categoryDto, date), true)
    }

    override fun gotoCategory() {
        changeFragment(CategoryFragment.getInstance(), true)
    }


}