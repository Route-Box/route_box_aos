package com.daval.routebox.presentation.utils.picker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
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

        setNavigation()
        initClickListeners()

        return binding.root
    }

    private fun setNavigation() {
        val safeInitialDate = initialDate.toString()
        val action = CalendarDateFragmentDirections.actionCalendarDateFragmentSelf(
            isStartDate = isStartDate,
            initialDate = safeInitialDate,
            setPrevDateDisable = setPrevDateDisable
        )

        parentFragment?.findNavController()?.navigate(action) ?: run {
            Log.e("CalendarBottomSheet", "parentFragment is null")
        }

        parentFragmentManager.setFragmentResultListener("calendarResult", this) { _, bundle ->
            Log.d("CalendarBottomSheet", "setFragmentResultListener()")
            val selectedDate = bundle.getString("selectedDate") ?: return@setFragmentResultListener
            listener.onDateReceived(isStartDate, LocalDate.parse(selectedDate))
            dismiss() // BottomSheet 닫기
        }
    }

    private fun initClickListeners() {
        // 닫기 버튼 클릭
        binding.calendarCloseIv.setOnClickListener {
            dismiss() // 창 닫기
        }
    }
}