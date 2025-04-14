package com.daval.routebox.presentation.utils.picker

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate

interface DateClickListener {
    fun onDateReceived(isStartDate: Boolean, date: LocalDate)
}

@RequiresApi(Build.VERSION_CODES.O)
class CalendarBottomSheet(
    private var listener: DateClickListener,
    private var setPrevDateDisable: Boolean,
    var isStartDate: Boolean,
    private var initialDate: LocalDate
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCalendarBinding.inflate(inflater, container, false)

        initClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavigation()
    }

    private fun setNavigation() {
        val fragment = CalendarDateFragment.newInstance(
            isStartDate, setPrevDateDisable, initialDate,
            object : DateClickListener {
                override fun onDateReceived(isStartDate: Boolean, date: LocalDate) {
                    listener.onDateReceived(isStartDate, date)
                    dismiss() // 날짜 전달 후 바텀시트 닫기
                }
            }
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.calendar_frm, fragment)
            .commitAllowingStateLoss()
    }

    private fun initClickListeners() {
        // 닫기 버튼 클릭
        binding.calendarCloseIv.setOnClickListener {
            dismiss() // 창 닫기
        }
    }
}