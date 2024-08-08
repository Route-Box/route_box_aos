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
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateActivity : AppCompatActivity(), DateClickListener {
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
        // 시작 날짜
        binding.routeCreateStartDateTv.setOnClickListener {
            showCalendarBottomSheet(true, viewModel.startDate.value!!)
        }

        // 종료 날짜
        binding.routeCreateEndDateTv.setOnClickListener {
            showCalendarBottomSheet(false, viewModel.endDate.value!!)
        }
    }

    private fun showCalendarBottomSheet(isStartDate: Boolean, date: LocalDate) {
        val calendarBottomSheet = CalendarBottomSheet(this, isStartDate, date)
        calendarBottomSheet.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        }
        calendarBottomSheet.show(this.supportFragmentManager, calendarBottomSheet.tag)
    }

    override fun onDateReceived(isStartDate: Boolean, date: LocalDate) {
        viewModel.updateDate(isStartDate, date)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val TODAY: LocalDate = LocalDate.now()
    }
}