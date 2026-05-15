package com.example.nammapustaka.database

import androidx.room.TypeConverter
import java.util.Date

/** Room type converters for [Date] persistence. */
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millis: Long?): Date? = millis?.let { Date(it) }
}
