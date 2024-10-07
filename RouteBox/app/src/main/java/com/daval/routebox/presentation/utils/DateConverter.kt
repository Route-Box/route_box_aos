package com.daval.routebox.presentation.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.daval.routebox.presentation.utils.picker.TimePickerBottomSheet.Companion.MINUTE_FORMAT
import java.lang.String.format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
object DateConverter {
    private const val YEAR_MONTH_PATTERN = "yyyy년 M월"
    private const val DATE_PATTERN = "yy년 M월 d일"
    private const val DATE_API_PATTERN = "yyyy-MM-dd"
    private const val TIME_PLACEHOLDER = "시간 선택"
    private const val CREATE_DATE_PATTERN = "yyyy.MM.dd"
    private const val TIME_DELIMINATOR = ":"

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

    fun getAPIFormattedTime(timePair: Pair<Int, Int>): String {
        return "${format(MINUTE_FORMAT, timePair.first)}${TIME_DELIMINATOR}${format(MINUTE_FORMAT, timePair.second)}"
    }

    // 로컬에서 선택한 시간을 서버 전송 형태로 벼경
    fun convertDateAndTimeToUTCString(date: LocalDate, timePair: Pair<Int, Int>): String {
        return convertKSTToUTC(getAPIFormattedDateAndTime(date, timePair))
    }

    private fun getAPIFormattedDateAndTime(date: LocalDate, timePair: Pair<Int, Int>): String {
        return "${getAPIFormattedDate(date)}T${format(MINUTE_FORMAT, timePair.first)}:${format(MINUTE_FORMAT, timePair.second)}:00" // "2024-08-28T14:11:52" 형태의 서버 데이터로 변환
    }

    private fun convertKSTToUTC(kstDateTime: String): String {
        // "yyyy-MM-ddThh:mm:ss" 형식의 KST 시간을 LocalDateTime으로 파싱
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val localDateTime = LocalDateTime.parse(kstDateTime, formatter)

        // KST (Asia/Seoul) 시간을 ZonedDateTime으로 변환
        val kstZonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"))

        // UTC 시간대로 변환
        val utcZonedDateTime = kstZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"))

        // UTC 시간을 "yyyy-MM-ddThh:mm:ss" 형태의 문자열로 포맷
        return utcZonedDateTime.format(formatter)
    }

    // KST 시간을 서버 포멧으로 변경
    fun convertKSTLocalDateTimeToUTCString(kstDateTime: LocalDateTime): String {
        // KST (Asia/Seoul) 시간을 ZonedDateTime으로 변환
        val kstZonedDateTime = kstDateTime.atZone(ZoneId.of("Asia/Seoul"))

        // UTC 시간대로 변환
        val utcZonedDateTime = kstZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"))

        // UTC 시간을 "2024-08-28T14:11:52" 형태의 문자열로 포맷
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return utcZonedDateTime.format(formatter)
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
        return "${timePair.first}${TIME_DELIMINATOR}${format(MINUTE_FORMAT, timePair.second)}"
    }

    fun convertDateStringToLocalDate(dateStr: String): LocalDate { // yyyy-MM-dd 형태의 시간
        val formatter = DateTimeFormatter.ofPattern(DATE_API_PATTERN) // 변환할 형식 정의
        return LocalDate.parse(dateStr, formatter) // 문자열을 LocalDate로 변환
    }

    fun convertTimeStringToIntPair(timeStr: String): Pair<Int, Int> { // hh:mm 형태의 시간
        val splitString = timeStr.split(TIME_DELIMINATOR)
        return Pair(splitString[0].toInt(), splitString[1].toInt())
    }
}