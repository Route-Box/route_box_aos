package com.daval.routebox.presentation.ui.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemInquiryHistoryBinding
import com.daval.routebox.domain.model.Inquiry

class InquiryHistoryRVAdapter(
    private var inquiryList: ArrayList<Inquiry>
): RecyclerView.Adapter<InquiryHistoryRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: InquiryItemClickListener

    fun setInquiryClickListener(itemClickListener: InquiryItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface InquiryItemClickListener {
        fun onItemClick(inquiry: Inquiry)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemInquiryHistoryBinding = ItemInquiryHistoryBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = inquiryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(inquiryList[position])
        holder.apply {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(inquiryList[position])
            }
        }
    }

    inner class ViewHolder(val binding: ItemInquiryHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(inquiry: Inquiry) {
            binding.inquiry = inquiry
        }
    }
}