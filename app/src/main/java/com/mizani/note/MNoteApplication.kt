package com.mizani.note

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.mizani.note.data.repository.CategoryRepositoryImpl
import com.mizani.note.data.LocalDatabase
import com.mizani.note.domain.repository.CategoryRepository
import com.mizani.note.domain.repository.NoteRepository
import com.mizani.note.data.repository.NoteRepositoryImpl
import com.mizani.note.presentation.fragments.add.NoteAddViewModel
import com.mizani.note.presentation.fragments.detaillist.DetailListViewModel
import com.mizani.note.presentation.fragments.note.NoteViewModel
import com.mizani.note.presentation.fragments.update.NoteUpdateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

class MNoteApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        startKoin {
            androidContext(this@MNoteApplication)
            modules(mutableListOf<Module>().apply {
                add(getModule())
            })
        }
    }

    private fun getModule() = module {
        single {
            Room.databaseBuilder(
                androidContext(),
                LocalDatabase::class.java,
                "note_database"
            ).build()
        }

        single {
            val database = get<LocalDatabase>()
            database.getNote()
        }

        single {
            val database = get<LocalDatabase>()
            database.getCategory()
        }

        single {
            NoteRepositoryImpl(get())
        } bind NoteRepository::class

        single {
            CategoryRepositoryImpl(get())
        } bind CategoryRepository::class

        viewModel {
            NoteViewModel(get(), get())
        }

        viewModel {
            NoteAddViewModel(get(), get())
        }

        viewModel {
            NoteUpdateViewModel(get(), get())
        }

        viewModel {
            DetailListViewModel(get())
        }
    }

}