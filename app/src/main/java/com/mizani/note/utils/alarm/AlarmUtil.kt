package com.mizani.note.utils.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import com.mizani.note.data.dto.NoteDto
import java.util.*

object AlarmUtil {

    const val ALARM_REQUEST_CODE = 1010
    const val ALARM_REQUEST = "ALARM_REQUEST"
    const val ALARM_NOTE_DATA = "ALARM_NOTE_DATA"

    private fun convert(date: Date, isRepeated: Boolean): Date {
        val calendar = Calendar.getInstance()
        val current = Calendar.getInstance()
        calendar.time = date
        if (isRepeated) {
            calendar.set(Calendar.YEAR, current.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, current.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH))
        }
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getPendingIntent(context: Context, intent: Intent, requestCode: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, requestCode, intent, FLAG_MUTABLE)
    }

    fun setAlarm(context: Context, pendingIntent: PendingIntent, date: Date, isRepeated: Boolean) {
        val convertedDate = convert(date, isRepeated).time
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (isRepeated) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, convertedDate, AlarmManager.INTERVAL_DAY, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, convertedDate, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun getIntentNote(context: Context, note: NoteDto): Intent {
        val intent = Intent(context, AlarmReceiver::class.java).also {
            it.putExtra(ALARM_REQUEST, ALARM_REQUEST_CODE)
            it.putExtra(ALARM_NOTE_DATA, note)
        }
        return intent
    }

}