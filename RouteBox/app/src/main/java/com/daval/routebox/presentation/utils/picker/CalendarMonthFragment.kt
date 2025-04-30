package com.daval.routebox.presentation.utils.picker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.daval.routebox.databinding.FragmentCalendarMonthBinding
import com.daval.routebox.presentation.utils.DateConverter
import com.daval.routebox.presentation.utils.DateConverter.MONTH_PATTERN
import com.daval.routebox.presentation.utils.DateConverter.YEAR_PATTERN
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class CalendarMonthFragment: Fragment() {

    private lateinit var binding: FragmentCalendarMonthBinding

    private var setPrevDateDisable: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarMonthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDate()
    }

    private fun initDate() {
        val currentDate = LocalDate.now()

        binding.calendarYearTv.text = DateConverter.getFormattedText(currentDate, YEAR_PATTERN)
        binding.calendarCurrentMonthTv.text = DateConverter.getFormattedText(currentDate, MONTH_PATTERN)
    }

    companion object {
        private const val SCHEDULE_CLICKED_DATE_FORMAT = "E월"

        fun newInstance(setPrevDateDisable: Boolean): CalendarMonthFragment {
            val fragment = CalendarMonthFragment()
            fragment.setPrevDateDisable = setPrevDateDisable
            return fragment
        }
    }
}