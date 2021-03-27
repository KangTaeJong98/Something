package com.taetae98.something.utility

import androidx.room.TypeConverter

class TimeTypeConverter {
    @TypeConverter
    fun timeToLong(time: Time): Long {
        return time.timeInMillis
    }

    @TypeConverter
    fun longToTime(time: Long): Time {
        return Time(time)
    }
}