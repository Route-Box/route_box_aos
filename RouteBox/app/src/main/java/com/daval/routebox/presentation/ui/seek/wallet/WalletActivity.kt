package com.daval.routebox.presentation.ui.seek.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityWalletBinding
import com.daval.routebox.domain.model.History
import com.daval.routebox.presentation.ui.seek.wallet.adapter.PointHistoryRVAdapter

class WalletActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var historyRVAdapter: PointHistoryRVAdapter
    private var historyList = arrayListOf<History>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet)
        binding.apply {
            
        }
        initClickListener()
        setAdapter()
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
            // TODO: 인앱 결제 기능 추가 했을 때 아래 화면으로 이동 
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
}