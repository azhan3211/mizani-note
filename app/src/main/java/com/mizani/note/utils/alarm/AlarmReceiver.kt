package com.mizani.note.utils.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mizani.note.R
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.domain.repository.NoteRepository
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.NotificationUtils
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
                            val intent = AlarmUtil.getIntentNote(context, it)
                            val pendingIntent =
                                AlarmUtil.getPendingIntent(context, intent, it.id?.toInt() ?: 0)
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
            val note = intent.getSerializableExtra(AlarmUtil.ALARM_NOTE_DATA) as NoteDto
            Toast.makeText(context, note.title, Toast.LENGTH_SHORT).show()
            context?.let {
                showNotification(it, note)
            }
        }
    }

    private fun showNotification(context: Context, noteDto: NoteDto) {
        var builder = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(noteDto.title)
            .setContentText(noteDto.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            this.notify(noteDto.id?.toInt().orZero(), builder)
        }

    }

}