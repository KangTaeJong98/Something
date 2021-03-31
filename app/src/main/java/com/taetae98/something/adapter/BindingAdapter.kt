package com.taetae98.something.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.taetae98.something.dto.ToDo
import com.taetae98.something.utility.LocaleFactory
import com.taetae98.something.utility.Time
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("todoTerm")
    fun setToDoTerm(view: TextView, todo: ToDo?) {
        if (todo == null) {
            view.text = ""
            return
        }

        val format = SimpleDateFormat.getDateInstance()
        if (todo.beginTime == todo.endTime) {
            view.text = format.format(todo.beginTime.timeInMillis)
        } else {
            val result = format.format(todo.beginTime.timeInMillis) + " ~ " + format.format(todo.endTime.timeInMillis)
            view.text = result
        }
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(view: TextView, time: Time?) {
        if (time == null) {
            view.text = ""
            return
        }

        val format = SimpleDateFormat.getDateInstance()
        view.text = format.format(time.timeInMillis)
    }

    @JvmStatic
    @BindingAdapter("calendarHead")
    fun setCalendarHead(view: TextView, time: Time?) {
        if (time == null) {
            return
        }

        val locale = LocaleFactory.getLocale()
        if (locale == Locale.ENGLISH) {
            val format = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            view.text = format.format(time.time)
        } else {
            val format = SimpleDateFormat("yyyy. MM.", Locale.KOREAN)
            view.text = format.format(time.time)
        }
    }
}