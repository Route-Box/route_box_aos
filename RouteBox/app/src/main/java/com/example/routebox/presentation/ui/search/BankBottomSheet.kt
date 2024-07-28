package com.example.routebox.presentation.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.BottomSheetBankBinding
import com.example.routebox.domain.model.Bank
import com.example.routebox.presentation.ui.search.adapter.BankRVAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BankBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetBankBinding
    private lateinit var bankRVAdapter: BankRVAdapter
    private lateinit var dialogFinishListener: OnDialogFinishListener
    private var selectBank: Bank? = null

    private var bankList = arrayListOf<Bank>()

    interface OnDialogFinishListener {
        fun finish(data: Bank?)
    }

    fun setOnDialogFinishListener(listener: OnDialogFinishListener) {
        dialogFinishListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetBankBinding.inflate(inflater, container, false)

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
            selectBank = null
            onDestroyView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogFinishListener.finish(selectBank)
        Log.d("BANK-TEST", "selectBank = ${selectBank}")
    }
}