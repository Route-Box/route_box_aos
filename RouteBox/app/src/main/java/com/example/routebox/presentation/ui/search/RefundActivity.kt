package com.example.routebox.presentation.ui.search

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRefundBinding
import com.example.routebox.domain.model.Bank

class RefundActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRefundBinding
    private var checkNext = arrayListOf(false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refund)

        initClickListener()
        initTextChangeListener()
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
                        checkNext[2] = true
                        checkNextBtn()
                    } else {
                        binding.bankLogo.visibility = View.GONE
                        binding.enterBank.text = getString(R.string.enter_bank)
                        binding.enterBank.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.gray5))
                        checkNext[2] = false
                    }
                }
            })
        }

        binding.pointEraseIv.setOnClickListener {
            binding.enterPointEt.setText("")
            checkPointValid(true)
        }

        binding.agreeRefundCv.setOnClickListener {
            checkNext[4] = !checkNext[4]

            if (!checkNext[4]) {
                binding.agreeRefundIv.setImageResource(R.drawable.ic_term_check_x)
                checkNextBtn()
            }
            else {
                binding.agreeRefundIv.setImageResource(R.drawable.ic_term_check_o)
                checkNextBtn()
            }
        }
    }

    private fun initTextChangeListener() {
        binding.enterPointEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.enterPointEt.text.isNotEmpty()) {
                    if (Integer.parseInt(binding.enterPointEt.text.toString()) % 5000 != 0) {
                        checkPointValid(false)
                        checkNext[0] = false
                    } else {
                        checkPointValid(true)
                        checkNext[0] = true
                    }

                    checkPointEmpty(false)
                } else {
                    checkPointValid(false)
                    checkPointEmpty(true)
                    checkNext[0] = false
                }

                checkNextBtn()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })

        binding.ownerEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                checkNext[1] = binding.ownerEt.text.isNotEmpty()
                checkNextBtn()
            }
        })

        binding.accountNumberEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                checkNext[3] = binding.accountNumberEt.text.isNotEmpty()
                checkNextBtn()
            }
        })
    }

    private fun checkPointValid(isValid: Boolean) {
        if (isValid) {
            binding.refundPointTitle.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.title_black))
            binding.refundRule.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.gray5))
        } else {
            binding.refundPointTitle.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.red1_warning))
            binding.refundRule.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.red1_warning))
        }
    }

    private fun checkPointEmpty(empty: Boolean) {
        if (empty) {
            binding.pTv.visibility = View.VISIBLE
            binding.pointEraseIv.visibility = View.GONE
            binding.refundPointTitle.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.title_black))
            binding.refundRule.setTextColor(ContextCompat.getColor(this@RefundActivity, R.color.gray5))
        } else {
            binding.pTv.visibility = View.GONE
            binding.pointEraseIv.visibility = View.VISIBLE
        }
    }

    private fun checkNextBtn() {
        binding.nextBtn.isEnabled = !checkNext.contains(false)
    }
}