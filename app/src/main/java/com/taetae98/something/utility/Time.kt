package com.taetae98.something.utility

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Time : Parcelable, Comparable<Time> {
    private val calendar: GregorianCalendar = GregorianCalendar()

    var timeInMillis: Long
        get() {
            return calendar.timeInMillis
        }
        set(value) {
            calendar.timeInMillis = value
        }
    
    var year: Int
        get() {
            return calendar.get(Calendar.YEAR)
        }
        set(value) {
            calendar.set(Calendar.YEAR, value)
        }

    var month: Int
        get() {
            return calendar.get(Calendar.MONTH)
        }
        set(value) {
            calendar.set(Calendar.MONTH, value)
        }

    var dayOfMonth: Int
        get() {
            return calendar.get(Calendar.DAY_OF_MONTH)
        }
        set(value) {
            calendar.set(Calendar.DAY_OF_MONTH, value)
        }

    var dayOfWeek: Int
        get() {
            return calendar.get(Calendar.DAY_OF_WEEK)
        }
        set(value) {
            calendar.set(Calendar.DAY_OF_WEEK, value)
        }

    constructor() {
        timeInMillis = System.currentTimeMillis()
        init()
    }

    constructor(time: Long = System.currentTimeMillis()) {
        timeInMillis = time
        init()
    }

    override fun compareTo(other: Time): Int {
        return compareValues(timeInMillis, other.timeInMillis)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Time) {
            return timeInMillis == other.timeInMillis
        }

        return super.equals(other)
    }

    private fun init() {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun set(field: Int, value: Int) {
        GregorianCalendar(year, month, dayOfMonth).apply {
            add(field, value - get(field))
        }.also {
            timeInMillis = it.timeInMillis
        }
    }

    constructor(parcel: Parcel) {
        timeInMillis = parcel.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(timeInMillis)
    }

    override fun hashCode(): Int {
        return timeInMillis.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<Time> {
        override fun createFromParcel(parcel: Parcel): Time {
            return Time(parcel)
        }

        override fun newArray(size: Int): Array<Time?> {
            return arrayOfNulls(size)
        }
    }
}