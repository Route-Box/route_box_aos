package com.daval.routebox.presentation.utils.picker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.daval.routebox.databinding.BottomSheetCalendarBinding
import com.daval.routebox.presentation.utils.DateConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.YearMonth

interface DateClickListener {
    fun onDateReceived(isStartDate: Boolean, date: LocalDate)
}

@RequiresApi(Build.VERSION_CODES.O)
class CalendarBottomSheet(private var listner: DateClickListener, private var setPrevDateDisable: Boolean, var isStartDate: Boolean, private var initialDate: LocalDate) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCalendarBinding
    private var criteriaDate = this.initialDate // 캘린더 날짜를 가져오는 기준 일자

    private lateinit var calendarAdapter: CalendarRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCalendarBinding.inflate(inflater, container, false)

        initClickListeners()
        setAdapter()

        return binding.root
    }


    private fun initClickListeners() {
        /* 화살표 눌러서 월 이동 */
        binding.calendarPreviousMonthIv.setOnClickListener { // 이전 달
            setCalendarDate(-1)
        }
        binding.calendarNextMonthIv.setOnClickListener { // 다음 달
            setCalendarDate(+1)
        }

        // 닫기 버튼 클릭
        binding.calendarCloseIv.setOnClickListener {
            dismiss() // 창 닫기
        }
    }

    // 날짜 적용 함수
    private fun setAdapter() {
        // 어댑터 초기화
        calendarAdapter = CalendarRVAdapter(setPrevDateDisable, getSelectedDatePosition(), initialDate.monthValue)
        binding.calendarDateRv.apply {
            layoutManager = GridLayoutManager(requireContext(), DAY_OF_WEEK)
            adapter = calendarAdapter
        }
        setCalendarDate(0)
        // 클릭 이벤트
        calendarAdapter.setMyDateClickListener(object: CalendarRVAdapter.MyDateClickListener{
            override fun onDateClick(selectedDate: LocalDate) {
                listner.onDateReceived(isStartDate, selectedDate) // 날짜 전달
                dismiss() // 뒤로가기
            }
        })
    }

    private fun setCalendarDate(direct: Long) {
        criteriaDate = criteriaDate.plusMonths(direct)
        // 상단 날짜 세팅
        binding.calendarYearMonthTv.text = DateConverter.getFormattedYearMonth(criteriaDate)
        calendarAdapter.addDateList(dayInMonthArr(criteriaDate))
    }

    // 날짜 생성
    private fun dayInMonthArr(date: LocalDate): ArrayList<LocalDate?> {
        val dateList = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)

        // 월의 시작일
        val monthFirstDate = criteriaDate.withDayOfMonth(1)
        // 월 첫 날의 요일 (일요일=0, ... ,월요일=6)
        val dayOfMonthFirstDate = monthFirstDate.dayOfWeek.value % DAY_OF_WEEK
        // 월의 종료일
        val monthLastDate = yearMonth.lengthOfMonth()

        for (i in 1..DAY_OF_WEEK * 6) { // 6줄짜리 달력
            if (dayOfMonthFirstDate == SUNDAY) { // 일~토 달력에서 1일이 일요일일 때, 첫째주가 비는 현상 제거
                if (i <= monthLastDate){
                    dateList.add(LocalDate.of(date.year, date.monthValue, i))
                }
                else {
                    dateList.add(null)
                }
            } else {
                if (i > dayOfMonthFirstDate && i < (monthLastDate + dayOfMonthFirstDate)) {
                    dateList.add(LocalDate.of(date.year, date.monthValue, i - dayOfMonthFirstDate))
                } else {
                    dateList.add(null)
                }
            }
        }

        return dateList
    }

    private fun getSelectedDatePosition(): Int {
        // 월 첫 날의 요일 구하기
        val dayOfWeek = initialDate.withDayOfMonth(1).dayOfWeek.value % DAY_OF_WEEK
        // 초기 날짜의 포지션 계산
        return initialDate.dayOfMonth + dayOfWeek - 1
    }

    companion object {
        const val DAY_OF_WEEK = 7 // 일주일
        const val SUNDAY = 0
    }
}