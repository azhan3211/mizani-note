package com.mizani.note.presentation.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar

object DateTimeDialog {

    fun timePickerDialog(context: Context, action: (hour: Int, minute: Int) -> Unit): TimePickerDialog {
        return TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                action.invoke(hourOfDay, minute)
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
    }

    fun datePickerDialog(context: Context, action: (year: Int, month: Int, day: Int) -> Unit): DatePickerDialog {
        return DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                action.invoke(year, month, dayOfMonth)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
        )
    }

}