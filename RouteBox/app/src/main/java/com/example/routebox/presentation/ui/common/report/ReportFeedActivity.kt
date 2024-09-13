package com.example.routebox.presentation.ui.common.report

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityReportFeedBinding

class ReportFeedActivity: AppCompatActivity() {

    private lateinit var binding: ActivityReportFeedBinding
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_feed)

        binding.apply {
            viewModel = this@ReportFeedActivity.viewModel
            lifecycleOwner = this@ReportFeedActivity
        }

        initClickListeners()
        initObserve()
    }

    private fun initClickListeners() {
        binding.reportFeedBtn.setOnClickListener {
            //TODO: 신고하기 API 연동
            finish()
        }
    }

    private fun initObserve() {
        viewModel.etcReasonDirectInput.observe(this) { content ->
            // 신고 버튼 활성화 상태 체크
            viewModel.checkIsReportButtonEnable()
        }
    }
}