package com.example.routebox.presentation.ui.common.report

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityReportFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        viewModel.routeId = intent.getIntExtra("routeId", 0)
        initClickListeners()
        initObserve()
    }

    private fun initClickListeners() {
        binding.reportFeedBtn.setOnClickListener {
            viewModel.tryReportFeed()
        }
    }

    private fun initObserve() {
        viewModel.etcReasonDirectInput.observe(this) { content ->
            // 신고 버튼 활성화 상태 체크
            viewModel.checkIsReportButtonEnable()
        }

        viewModel.isReportSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}