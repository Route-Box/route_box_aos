package com.example.routebox.presentation.ui.route.record

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteCreateBinding
import com.example.routebox.presentation.utils.picker.CalendarBottomSheet
import com.example.routebox.presentation.utils.picker.DateClickListener
import com.example.routebox.presentation.utils.picker.TimePickerBottomSheet
import com.example.routebox.presentation.utils.picker.TimeChangedListener
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateActivity : AppCompatActivity(), DateClickListener, TimeChangedListener {
    private lateinit var binding: ActivityRouteCreateBinding

    private val viewModel: RouteCreateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_create)

        binding.apply {
            viewModel = this@RouteCreateActivity.viewModel
            lifecycleOwner = this@RouteCreateActivity
        }

        initClickListeners()
    }

    private fun initClickListeners() {
        // 뒤로가기
        binding.routeCreateBackIv.setOnClickListener {
            finish()
        }

        // 다음 버튼
        binding.routeCreateNextBtn.setOnClickListener {
            //TODO: 루트 기록 화면으로 이동
        }

        // 시작 날짜
        binding.routeCreateStartDateTv.setOnClickListener {
            showCalendarBottomSheet(true, viewModel.startDate.value!!)
        }

        // 종료 날짜
        binding.routeCreateEndDateTv.setOnClickListener {
            showCalendarBottomSheet(false, viewModel.endDate.value!!)
        }

        // 시작 시간
        binding.routeCreateStartTimeTv.setOnClickListener {
            showTimePickerBottomSheet(true, viewModel.startTimePair.value)
        }

        // 종료 시간
        binding.routeCreateEndTimeTv.setOnClickListener {
            showTimePickerBottomSheet(false, viewModel.endTimePair.value)
        }
    }

    private fun showCalendarBottomSheet(isStartDate: Boolean, date: LocalDate) {
        val calendarBottomSheet = CalendarBottomSheet(this, isStartDate, date)
        calendarBottomSheet.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        }
        calendarBottomSheet.show(this.supportFragmentManager, calendarBottomSheet.tag)
    }

    private fun showTimePickerBottomSheet(isStartTime: Boolean, initTime: Pair<Int, Int>?) {
        val pickerBottomSheet = TimePickerBottomSheet(
            this,
            isStartTime,
            initTime?.first ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY), // 선택한 시간 정보가 없다면 현재 hour로 피커 초기화
            initTime?.second ?: Calendar.getInstance().get(Calendar.MINUTE) // 선택한 시간 정보가 없다면 현재 minute로 피커 초기화
        )
        pickerBottomSheet.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        }
        pickerBottomSheet.show(this.supportFragmentManager, pickerBottomSheet.tag)
    }

    override fun onDateReceived(isStartDate: Boolean, date: LocalDate) {
        viewModel.updateDate(isStartDate, date)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val TODAY: LocalDate = LocalDate.now()
    }

    override fun onTimeSelected(isStartTime: Boolean, hour: Int, minute: Int) {
        viewModel.updateTime(isStartTime, Pair(hour, minute))
    }
}