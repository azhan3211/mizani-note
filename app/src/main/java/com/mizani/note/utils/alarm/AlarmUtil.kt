package com.mizani.note.utils.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mizani.note.R
import com.mizani.note.data.dto.NoteDto
import com.mizani.note.utils.DataTypeExt.orZero
import com.mizani.note.utils.NotificationUtils
import java.util.Calendar
import java.util.Date

object AlarmUtil {

    const val ALARM_REQUEST_CODE = 1010
    const val ALARM_REQUEST = "ALARM_REQUEST"
    const val ALARM_NOTE_ID = "ALARM_NOTE_ID"

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
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                convertedDate,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, convertedDate, pendingIntent)
        }
    }

    fun showNotification(context: Context, noteDto: NoteDto) {
        var builder = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(noteDto.title)
            .setContentText(noteDto.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            this.notify(noteDto.id?.toInt().orZero(), builder)
        }
    }

    fun permissionAlarm(context: Context, callback: () -> Unit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms().not()) {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } else {
                callback.invoke()
            }
        } else {
            callback.invoke()
        }
    }

    fun cancelAlarm(context: Context, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun getIntentNote(context: Context, note: NoteDto): Intent {
        val intent = Intent(context, AlarmReceiver::class.java).also {
            it.putExtra(ALARM_REQUEST, ALARM_REQUEST_CODE)
            it.putExtra(ALARM_NOTE_ID, note.id)
        }
        return intent
    }

}