package com.example.routebox.presentation.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.routebox.presentation.utils.picker.TimePickerBottomSheet.Companion.MINUTE_FORMAT
import java.lang.String.format
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object DateConverter {
    private const val YEAR_MONTH_PATTERN = "yyyy년 M월"
    private const val DATE_PATTERN = "yy년 M월 d일"
    private const val TIME_PLACEHOLDER = "시간 선택"

    fun getFormattedYearMonth(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(YEAR_MONTH_PATTERN))
    }

    @JvmStatic
    fun getFormattedDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getFormattedTime(timePair: Pair<Int, Int>?): String {
        if (timePair == null) return TIME_PLACEHOLDER
        return "${timePair.first}:${format(MINUTE_FORMAT, timePair.second)}"
    }
}