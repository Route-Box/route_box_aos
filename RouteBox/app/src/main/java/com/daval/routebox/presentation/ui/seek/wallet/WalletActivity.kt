package com.daval.routebox.presentation.ui.seek.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityWalletBinding
import com.daval.routebox.domain.model.PointHistory
import com.daval.routebox.presentation.ui.seek.SeekViewModel
import com.daval.routebox.presentation.ui.seek.wallet.adapter.PointHistoryRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var historyRVAdapter: PointHistoryRVAdapter
    private var historyList = arrayListOf<PointHistory>()
    private val viewModel: SeekViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet)
        binding.apply {
            viewModel = this@WalletActivity.viewModel
            lifecycleOwner = this@WalletActivity
        }

        setAdapter()
        initData()
        initObserve()
        initClickListener()
        initScrollListener()
    }

    private fun initData() {
        viewModel.getMyInformation()
        viewModel.getPointHistories()
    }

    private fun initObserve() {
        viewModel.pointHistoryList.observe(this@WalletActivity) {
            historyRVAdapter.addItems(viewModel.returnPointHistory())
        }
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.chargeCv.setOnClickListener {
            startActivity(Intent(this, ChargeActivity::class.java))
        }

        binding.refundCv.setOnClickListener {
            Toast.makeText(this, ContextCompat.getString(this, R.string.update), Toast.LENGTH_LONG).show()
            // TODO: 환급 기능 추가 했을 때 아래 화면으로 이동
            // startActivity(Intent(this, RefundActivity::class.java))
        }
    }

    private fun setAdapter() {
        historyRVAdapter = PointHistoryRVAdapter(historyList)
        binding.pointHistoryRv.adapter = historyRVAdapter
        binding.pointHistoryRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if (historyList.size == 0) {
            binding.emptyCl.visibility = View.VISIBLE
        } else {
            binding.emptyCl.visibility = View.GONE
        }
    }

    // TODO: 테스트 필요 (구매하기 API 연동 후 테스트 예정)
    private fun initScrollListener() {
        binding.pointHistoryRv.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.pointHistoryRv.canScrollVertically(1)) {
                if (viewModel.pointHistoryList.value?.content?.size == 10) {
                    viewModel.getPointHistories()
                }
            }
        }
    }
}