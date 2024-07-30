package com.example.routebox.presentation.ui.search.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.R
import com.example.routebox.databinding.ItemFilterOptionBinding
import com.example.routebox.domain.model.FilterOption

class FilterOptionsRVAdapter: RecyclerView.Adapter<FilterOptionsRVAdapter.ViewHolder>(){

    private var optionList = listOf<FilterOption>()
    private lateinit var mItemClickListener: MyItemClickListener
    private lateinit var context: Context
    private val selectedOptions = mutableSetOf<FilterOption>()

    fun setOptionClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addOption(optionList: List<FilterOption>) {
        this.optionList = optionList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllOptions() {
        this.selectedOptions.clear()
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int, isSelected: Boolean)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFilterOptionBinding = ItemFilterOptionBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        context = viewGroup.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(optionList[position])
        holder.itemView.setOnClickListener {
            val selectedOption = optionList[position]
            if (selectedOptions.contains(selectedOption)) {
                selectedOptions.remove(selectedOption)
                mItemClickListener.onItemClick(position, false)
            } else {
                selectedOptions.add(selectedOption)
                mItemClickListener.onItemClick(position, true)
            }
            holder.updateSelection(selectedOption)
        }
    }

    override fun getItemCount(): Int = optionList.size

    inner class ViewHolder(val binding: ItemFilterOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: FilterOption) {
            binding.option = option
            updateSelection(option)
        }

        fun updateSelection(option: FilterOption) {
            binding.itemFilterOptionTv.apply {
                if (selectedOptions.contains(option)) { // 선택 처리
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main)) // 선택된 배경색
                    setTextColor(ContextCompat.getColor(context, R.color.white)) // 선택된 텍스트 색상
                } else { // 선택 해제
                    backgroundTintList = null // 기본 배경색
                    setTextColor(ContextCompat.getColor(context, R.color.title_black)) // 기본 텍스트 색상
                }
            }
        }
    }
}