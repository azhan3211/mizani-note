package com.mizani.note.data

import androidx.room.TypeConverter
import java.util.*

class RoomConverter {

    @TypeConverter
    fun longToDate(date: Long) = Date(date)

    @TypeConverter
    fun dateToLong(date: Date) = date.time

}