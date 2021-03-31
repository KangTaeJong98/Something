package com.taetae98.something.utility

import java.util.*

object LocaleFactory {
    fun getLocale(): Locale {
        val locale = Locale.getDefault()
        return if (locale == Locale.KOREA || locale == Locale.KOREAN) {
            Locale.getDefault()
        } else {
            Locale.ENGLISH
        }
    }
}