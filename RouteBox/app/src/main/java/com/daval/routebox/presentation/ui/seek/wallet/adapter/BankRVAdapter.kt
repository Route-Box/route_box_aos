package com.daval.routebox.presentation.ui.seek.wallet.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.R
import com.daval.routebox.databinding.ItemBankBinding
import com.daval.routebox.domain.model.Bank

class BankRVAdapter(
    private val bankList: ArrayList<Bank>
): RecyclerView.Adapter<BankRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener
    private var selectedBank: Int = -1

    interface OnItemClickListener {
        fun onItemClick(data: Bank)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBankBinding = ItemBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(bankList[position])
        }

        holder.bind(bankList[position], position)
    }

    override fun getItemCount(): Int = bankList.size

    inner class ViewHolder(val binding: ItemBankBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: Bank, position: Int) {
            binding.bank = bank

            if (position == selectedBank) {
                binding.bankCv.strokeColor = ContextCompat.getColor(binding.root.context, R.color.app_name_color)
                binding.bankCv.setBackgroundColor(Color.parseColor("#1A21C8B6"))
            }
        }
    }

    fun selectedBankIndex(index: Int) {
        selectedBank = index
    }
}