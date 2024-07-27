package com.example.routebox.presentation.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.R
import com.example.routebox.databinding.ItemBankBinding
import com.example.routebox.domain.model.Bank

class BankRVAdapter(
    private val bankList: ArrayList<Bank>
): RecyclerView.Adapter<BankRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    //    private val bankList: ArrayList<Bank> = arrayListOf(
//        Bank("KB 국민", R.drawable.ic_bank_kb), Bank("IBK기업", R.drawable.ic_bank_ibk), Bank("NH농협", R.drawable.ic_bank_nh),
//        Bank("신한", R.drawable.ic_bank_shinhan), Bank("우리", R.drawable.ic_bank_woori), Bank("한국씨티", R.drawable.ic_bank_citi),
//        Bank("토스뱅크", R.drawable.ic_bank_toss), Bank("카카오뱅크", R.drawable.ic_bank_kakao), Bank("SC제일", R.drawable.ic_bank_sc),
//        Bank("하나", R.drawable.ic_bank_hana), Bank("대구", R.drawable.ic_bank_dgb), Bank("경남", R.drawable.ic_bank_bnk),
//        Bank("KDB산업", R.drawable.ic_bank_kdb), Bank("우체국", R.drawable.ic_bank_epost), Bank("수협", R.drawable.ic_bank_suhyup),
//        Bank("광주", R.drawable.ic_bank_jb), Bank("SBI저축은행", R.drawable.ic_bank_sbi), Bank("새마을금고", R.drawable.ic_bank_mg),
//        Bank("케이뱅크", R.drawable.ic_bank_k), Bank("부산", R.drawable.ic_bank_bnk), Bank("전북", R.drawable.ic_bank_jb),
//        Bank("제주", R.drawable.ic_bank_shinhan)
//    )

    interface OnItemClickListener {
        fun onItemClick(data: Bank)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankRVAdapter.ViewHolder {
        val binding: ItemBankBinding = ItemBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankRVAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(bankList[position])
        }

        holder.bind(bankList[position])
    }

    override fun getItemCount(): Int = bankList.size

    inner class ViewHolder(val binding: ItemBankBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: Bank) {
            binding.bank = bank
        }
    }
}