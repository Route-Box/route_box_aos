package com.example.routebox.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object DateConverter {
    private const val YEAR_MONTH_PATTERN = "yyyy년 M월"
    private const val DATE_PATTERN = "yy년 M월 d일"

    fun getFormattedYearMonth(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(YEAR_MONTH_PATTERN))
    }

    @JvmStatic
    fun getFormattedDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }
}