package com.example.routebox.presentation.ui.search

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRefundBinding
import com.example.routebox.domain.model.Bank

class RefundActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRefundBinding
    private var checkFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refund)

        initClickListener()
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.bankCl.setOnClickListener {
            val bankBottomSheet = BankBottomSheet()
            bankBottomSheet.show(supportFragmentManager, "BankBottomSheet")
            bankBottomSheet.setOnDialogFinishListener(object: BankBottomSheet.OnDialogFinishListener {
                override fun finish(data: Bank?) {
                    if (data != null) {
                        binding.bankLogo.visibility = View.VISIBLE
                        binding.bankLogo.setImageDrawable(data.bankImg)
                        binding.enterBank.text = " " + data.bankName
                        binding.enterBank.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.title_black))
                    } else {
                        binding.bankLogo.visibility = View.GONE
                        binding.enterBank.text = getString(R.string.enter_bank)
                        binding.enterBank.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.gray5))
                    }
                }
            })
        }

        binding.agreeRefundCv.setOnClickListener {
            if (checkFlag) binding.agreeRefundIv.setImageResource(R.drawable.ic_term_check_x)
            else binding.agreeRefundIv.setImageResource(R.drawable.ic_term_check_o)

            checkFlag = !checkFlag
        }
    }
}