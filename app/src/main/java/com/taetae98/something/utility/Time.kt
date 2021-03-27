package com.taetae98.something.utility

import java.util.*

class Time() : GregorianCalendar() {
    var year: Int
        get() {
            return get(YEAR)
        }
        set(value) {
            set(Calendar.YEAR, value)
        }

    var month: Int
        get() {
            return get(MONTH)
        }
        set(value) {
            set(Calendar.MONTH, value)
        }

    var dayOfMonth: Int
        get() {
            return get(DAY_OF_MONTH)
        }
        set(value) {
            set(Calendar.DAY_OF_MONTH, value)
        }

    var dayOfWeek: Int
        get() {
            return get(DAY_OF_WEEK)
        }
        set(value) {
            set(Calendar.DAY_OF_WEEK, value)
        }

    init {
        init()
    }

    constructor(time: Long) : this() {
        timeInMillis = time
        init()
    }

    private fun init() {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
    }

    override fun set(field: Int, value: Int) {
        GregorianCalendar(year, month, dayOfMonth).apply {
            add(field, value - get(field))
        }.also {
            timeInMillis = it.timeInMillis
        }
    }
}