package com.example.routebox.presentation.utils.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.annotation.IntRange
import com.example.routebox.databinding.BottomSheetTimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface TimeChangedListener {
    fun onTimeSelected(isStartTime: Boolean, hour: Int, minute: Int)
}

class TimePickerBottomSheet(private var listener: TimeChangedListener, private val isStartTime: Boolean, private val initHour: Int, private val initMinute: Int) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetTimePickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetTimePickerBinding.inflate(inflater, container, false)

        initPicker()
        initClickListeners()

        return binding.root
    }

    private fun initClickListeners() {
        // x 버튼
        binding.pickerCloseIv.setOnClickListener {
            dismiss() // 종료
        }

        // 저장 버튼
        binding.pickerSaveBtn.setOnClickListener {
            val selectedHour = binding.pickerTp.hour
            val selectedMinute = binding.pickerTp.minute * MINUTES_INTERVAL
            listener.onTimeSelected(isStartTime, selectedHour, selectedMinute) // 선택한 시간 넘기기
            dismiss()
        }
    }

    private fun initPicker() {
        // 시간 간격을 5분 단위로 설정
        binding.pickerTp.setTimeInterval(MINUTES_INTERVAL)

        // TimePicker에 초기 시간을 설정
        val adjustedMinute = initMinute / MINUTES_INTERVAL
        binding.pickerTp.apply {
            hour = initHour
            minute = adjustedMinute
        }
    }

    private fun TimePicker.setTimeInterval(
        @IntRange(from = 1, to = 30)
        timeInterval: Int = MINUTES_INTERVAL
    ) {
        try {
            // 분 단위 스피너 찾기
            val minutePicker = findViewById<NumberPicker>(
                resources.getIdentifier("minute", "id", "android")
            )

            // 5분 간격의 배열을 생성해 분 단위 스피너에 적용하기
            val minuteValues = Array(MINUTES_MAX / timeInterval) { (it * timeInterval).toString().padStart(2, '0') }
            minutePicker.minValue = MINUTES_MIN
            minutePicker.maxValue = MINUTES_MAX / timeInterval - 1
            minutePicker.displayedValues = minuteValues
        } catch (e: Exception) {
            e.printStackTrace()  // 필요에 따라 Log.e로 변경 가능
        }
    }

    companion object {
        const val MINUTES_INTERVAL = 5
        const val MINUTES_MIN = 0
        const val MINUTES_MAX = 60
        const val MINUTE_FORMAT = "%02d"
    }
}