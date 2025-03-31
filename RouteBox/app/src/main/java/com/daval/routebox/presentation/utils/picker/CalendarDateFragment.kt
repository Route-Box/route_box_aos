package com.daval.routebox.presentation.utils.picker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.daval.routebox.databinding.FragmentCalendarDateBinding
import com.daval.routebox.presentation.utils.DateConverter
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class CalendarDateFragment : Fragment() {

    private lateinit var binding: FragmentCalendarDateBinding

    private lateinit var calendarAdapter: CalendarRVAdapter
    private lateinit var navController: NavController

    private var isStartDate: Boolean = false

    private var initialDate: LocalDate = LocalDate.now() // 캘린더 날짜를 가져오는 기준 일자
    private var setPrevDateDisable: Boolean = true

    private var criteriaDate = this.initialDate // 캘린더 날짜를 가져오는 기준 일자

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val args = CalendarDateFragmentArgs.fromBundle(it)
            isStartDate = args.isStartDate
            initialDate = try {
                LocalDate.parse(args.initialDate)
            } catch (e: Exception) {
                LocalDate.now() // 오류 발생 시 현재 날짜 사용
            }
            setPrevDateDisable = args.setPrevDateDisable
            Log.d("CalendarDateFrag", "onCreate\nisStartDate: $isStartDate, initialDate: $initialDate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarDateBinding.inflate(inflater, container, false)
        navController = findNavController()

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
    }

    private fun setAdapter() {
        calendarAdapter = CalendarRVAdapter(setPrevDateDisable, getSelectedDatePosition(), initialDate.monthValue)
        binding.calendarDateRv.apply {
            layoutManager = GridLayoutManager(requireContext(), DAY_OF_WEEK)
            adapter = calendarAdapter
        }

        setCalendarDate(0)

        calendarAdapter.setMyDateClickListener(object : CalendarRVAdapter.MyDateClickListener {
            override fun onDateClick(selectedDate: LocalDate) {
                val result = Bundle().apply {
                    putString("selectedDate", selectedDate.toString())
                }

                parentFragmentManager.setFragmentResult("calendarResult", result)
                parentFragmentManager.popBackStack() // 현재 Fragment 닫기
            }
        })
    }

    // 날짜 생성
    private fun dayInMonthArr(date: LocalDate): ArrayList<LocalDate?> {
        val dateList = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)

        // 월의 시작일
        val monthFirstDate = initialDate.withDayOfMonth(1)
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
                if (i > dayOfMonthFirstDate && i <= (monthLastDate + dayOfMonthFirstDate)) {
                    dateList.add(LocalDate.of(date.year, date.monthValue, i - dayOfMonthFirstDate))
                } else {
                    dateList.add(null)
                }
            }
        }

        return dateList
    }

    private fun setCalendarDate(direct: Long) {
        criteriaDate = criteriaDate.plusMonths(direct)
        binding.calendarYearMonthTv.text = DateConverter.getFormattedYearMonth(criteriaDate)
        calendarAdapter.addDateList(dayInMonthArr(criteriaDate))
    }

    private fun getSelectedDatePosition(): Int {
        val dayOfWeek = initialDate.withDayOfMonth(1).dayOfWeek.value % DAY_OF_WEEK
        return initialDate.dayOfMonth + dayOfWeek - 1
    }

    companion object {
        const val DAY_OF_WEEK = 7 // 일주일
        const val SUNDAY = 0
    }
}