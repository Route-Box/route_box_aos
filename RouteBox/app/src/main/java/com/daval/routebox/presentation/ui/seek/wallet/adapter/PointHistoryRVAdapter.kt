package com.daval.routebox.presentation.ui.seek.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemPointHistoryBinding
import com.daval.routebox.domain.model.History

class PointHistoryRVAdapter(
    private val historyList: ArrayList<History>
): RecyclerView.Adapter<PointHistoryRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(data: History)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPointHistoryBinding = ItemPointHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    inner class ViewHolder(val binding: ItemPointHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.history = history
        }
    }
}