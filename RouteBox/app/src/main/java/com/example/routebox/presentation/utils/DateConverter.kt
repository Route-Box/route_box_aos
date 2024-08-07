package com.example.routebox.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object DateConverter {
    private const val DATE_PATTERN = "yy년 M월 d일"

    @JvmStatic
    fun getFormattedDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }
}