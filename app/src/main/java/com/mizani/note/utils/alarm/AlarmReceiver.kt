package com.mizani.note.utils.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mizani.note.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: NoteRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.equals("android.intent.action.BOOT_COMPLETED") == true) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val calendar = Calendar.getInstance()
                    val notes = repository.getAllActive(calendar.time)
                    notes.forEach {
                        context?.let { ctx ->
                            val alarmIntent = AlarmUtil.getIntentNote(ctx, it)
                            val pendingIntent =
                                AlarmUtil.getPendingIntent(ctx, alarmIntent, it.id?.toInt() ?: 0)
                            AlarmUtil.setAlarm(ctx, pendingIntent, it.date, it.isRepeated)
                        }
                    }
                } catch (e: Exception) {
                    cancel()
                }
            }
            return
        }
        if (intent?.getIntExtra(AlarmUtil.ALARM_REQUEST, -1) == AlarmUtil.ALARM_REQUEST_CODE) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    context?.let {
                        val noteId = intent.getLongExtra(AlarmUtil.ALARM_NOTE_ID, 0)
                        val note = repository.getNote(noteId)
                        this.launch(Dispatchers.Main) {
                            Toast.makeText(it, note.title, Toast.LENGTH_SHORT).show()
                        }
                        AlarmUtil.showNotification(it, note)
                    }
                } finally {
                    cancel()
                }
            }
        }
    }

}