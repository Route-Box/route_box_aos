package com.daval.routebox.presentation.ui.seek.wallet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetBankBinding
import com.daval.routebox.domain.model.Bank
import com.daval.routebox.presentation.ui.seek.wallet.adapter.BankRVAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BankBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetBankBinding
    private lateinit var bankRVAdapter: BankRVAdapter
    private lateinit var dialogFinishListener: OnDialogFinishListener
    private var selectBank: Bank? = null
    private var preSelectedBank: String = ""

    private var bankList = arrayListOf<Bank>()

    interface OnDialogFinishListener {
        fun finish(data: Bank?)
    }

    fun setOnDialogFinishListener(listener: OnDialogFinishListener) {
        dialogFinishListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetBankBinding.inflate(inflater, container, false)

        preSelectedBank = arguments?.getString("bankName").toString()

        setAdapter()
        initClickListener()

        bankList.addAll(arrayListOf(
            Bank("KB 국민", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_kb)!!), Bank("IBK기업", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_ibk)!!), Bank("NH농협", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_nh)!!),
            Bank("신한", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_shinhan)!!), Bank("우리", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_woori)!!), Bank("한국씨티", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_citi)!!),
            Bank("토스뱅크", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_toss)!!), Bank("카카오뱅크", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_kakao)!!), Bank("SC제일", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_sc)!!),
            Bank("하나", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_hana)!!), Bank("대구", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_dgb)!!), Bank("경남", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_bnk)!!),
            Bank("KDB산업", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_kdb)!!), Bank("우체국", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_epost)!!), Bank("수협", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_suhyup)!!),
            Bank("광주", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_jb)!!), Bank("SBI저축은행", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_sbi)!!), Bank("새마을금고", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_mg)!!),
            Bank("케이뱅크", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_k)!!), Bank("부산", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_bnk)!!), Bank("전북", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_jb)!!),
            Bank("제주", ContextCompat.getDrawable(binding.root.context, R.drawable.ic_bank_shinhan)!!)
        ))

        // 이전에 선택한 은행 확인 후 View 처리
        for (i in 0 until bankList.size) {
            if (bankList[i].bankName == preSelectedBank) {
                bankRVAdapter.selectedBankIndex(i)
                selectBank = Bank(bankList[i].bankName, bankList[i].bankImg)
                break
            }
        }

        return binding.root
    }

    private fun setAdapter() {
        bankRVAdapter = BankRVAdapter(bankList)
        binding.bankRv.adapter = bankRVAdapter
        binding.bankRv.layoutManager = GridLayoutManager(context, 3)
        bankRVAdapter.setOnItemClickListener(listener = object: BankRVAdapter.OnItemClickListener {
            override fun onItemClick(data: Bank) {
                selectBank = data
                onDestroyView()
            }
        })
    }

    private fun initClickListener() {
        binding.closeTv.setOnClickListener {
            if (preSelectedBank == "") selectBank = null
            onDestroyView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogFinishListener.finish(selectBank)
    }
}