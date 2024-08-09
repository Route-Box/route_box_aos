package com.example.routebox.presentation.utils.picker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.annotation.IntRange
import com.example.routebox.databinding.BottomSheetTimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.String.format

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

        binding.pickerCloseIv.setOnClickListener {
            dismiss() // 종료
        }

        initPicker()

        return binding.root
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

        binding.pickerTp.setOnTimeChangedListener { _, hourOfDay, minute ->
            val finalMinute = minute * MINUTES_INTERVAL
            listener.onTimeSelected(isStartTime, hourOfDay, finalMinute)
        }
    }

    private fun TimePicker.setTimeInterval(
        @IntRange(from = 1, to = 30)
        timeInterval: Int = MINUTES_INTERVAL
    ) {
        try {
            val minutePicker = findMinutePicker()
            minutePicker?.apply {
                minValue = MINUTES_MIN
                maxValue = (MINUTES_MAX / timeInterval) - 1
                displayedValues = getDisplayedValues(timeInterval)
            }
        } catch (e: Exception) {
            e.printStackTrace()  // 필요에 따라 Log.e로 변경 가능
        }
    }

    @SuppressLint("DefaultLocale")
    private fun TimePicker.getDisplayedValues(
        @IntRange(from = 1, to = 30)
        timeInterval: Int = MINUTES_INTERVAL
    ): Array<String> {
        return (MINUTES_MIN until MINUTES_MAX step timeInterval)
            .map { format(MINUTE_FORMAT, it) }
            .toTypedArray()
    }

    private fun TimePicker.getDisplayedMinutes(): Int {
        val minutePicker = findMinutePicker()
        return (minutePicker?.value ?: 0) * MINUTES_INTERVAL
    }

    @SuppressLint("PrivateApi")
    private fun TimePicker.findMinutePicker(): NumberPicker? {
        try {
            val classForId = Class.forName("com.android.internal.R\$id")
            val fieldId = classForId.getField("minute").getInt(null)
            return findViewById(fieldId) as? NumberPicker
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        const val MINUTES_INTERVAL = 5
        const val MINUTES_MIN = 0
        const val MINUTES_MAX = 60
        const val MINUTE_FORMAT = "%02d"
    }
}