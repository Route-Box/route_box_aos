package com.example.routebox.presentation.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.routebox.presentation.utils.picker.TimePickerBottomSheet.Companion.MINUTE_FORMAT
import java.lang.String.format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
object DateConverter {
    private const val YEAR_MONTH_PATTERN = "yyyy년 M월"
    private const val DATE_PATTERN = "yy년 M월 d일"
    private const val DATE_API_PATTERN = "yyyy-MM-dd"
    private const val TIME_PLACEHOLDER = "시간 선택"
    private const val CREATE_DATE_PATTERN = "yyyy.MM.dd"

    fun getFormattedYearMonth(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(YEAR_MONTH_PATTERN))
    }

    @JvmStatic
    fun getFormattedDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }

    fun getAPIFormattedDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(DATE_API_PATTERN))
    }

    fun getAPIFormattedDateAndTime(date: LocalDate, timePair: Pair<Int, Int>): String {
        return "${getAPIFormattedDate(date)}T${format(MINUTE_FORMAT, timePair.first)}:${format(MINUTE_FORMAT, timePair.second)}:00" // "2024-08-28T14:11:52" 형태의 서버 데이터로 변환
    }

    @JvmStatic
    fun getFormattedCreatedDateTime(serverDate: String): String { // "2024-08-28T14:11:52" 형태의 서버 데이터
        // 서버로부터 받은 날짜를 LocalDateTime으로 파싱
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val parsedDate = LocalDateTime.parse(serverDate, formatter)

        // UTC로 파싱된 LocalDateTime을 ZonedDateTime으로 변환하여 KST로 설정
        val kstDateTime = parsedDate.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
            .toLocalDateTime()

        // 현재 시간과의 차이를 계산
        val now = LocalDateTime.now()
        val hoursDifference = ChronoUnit.HOURS.between(kstDateTime, now)
        val daysDifference = ChronoUnit.DAYS.between(kstDateTime.toLocalDate(), now.toLocalDate())

        return when {
            hoursDifference in 0..23 -> "${hoursDifference}시간 전" // 올린 지 0~23시간 : 시간으로 표시
            daysDifference in 1..2 -> "${daysDifference}일 전" // 24~48시간 : 1일 전, 2일 전으로 표시
            else -> parsedDate.toLocalDate().format(DateTimeFormatter.ofPattern(CREATE_DATE_PATTERN)) // 이후는 날짜로 표시
        }
    }

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getFormattedTime(timePair: Pair<Int, Int>?): String {
        if (timePair == null) return TIME_PLACEHOLDER
        return "${timePair.first}:${format(MINUTE_FORMAT, timePair.second)}"
    }
}