package com.daval.routebox.presentation.ui.common.routeStyle

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.R
import com.daval.routebox.databinding.ItemFilterOptionBinding
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.FilterType

class FilterOptionsRVAdapter(private val canDuplicationSelect: Boolean): RecyclerView.Adapter<FilterOptionsRVAdapter.ViewHolder>(){

    private var optionList = listOf<FilterOption>()
    private lateinit var mItemClickListener: MyItemClickListener
    private lateinit var context: Context
    private var selectedOptions = mutableListOf<FilterOption>()

    fun setOptionClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addOption(optionList: List<FilterOption>) {
        this.optionList = optionList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initSelectedOptions(selectedOptionList: List<FilterOption>) {
        this.selectedOptions = selectedOptionList.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllOptions() {
        this.selectedOptions.clear()
        notifyDataSetChanged()
    }

    // selectedOptions에 현재 질문 유형이 몇 개 있는지 확인
    private fun getSameFilterTypeCount(selectedType: FilterType): Int {
        return selectedOptions.count { it.filterType == selectedType }
    }

    interface MyItemClickListener {
        fun onItemClick(selectedOption: FilterOption, isSelected: Boolean)
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
            if (!canDuplicationSelect) { // 단일 선택 처리 (루트 스타일 화면)
                if (selectedOptions.contains(selectedOption)) return@setOnClickListener
                if (getSameFilterTypeCount(selectedOption.filterType) >= selectedOption.filterType.maxSelectionCount) {
                    // 가장 먼저 선택한 항목을 삭제
                    val firstOptionToRemove = selectedOptions.firstOrNull { it.filterType == selectedOption.filterType }
                    firstOptionToRemove?.let { remoteOption ->
                        val currentTypeOptions = optionList.filter { it.filterType == selectedOption.filterType }
                        selectedOptions.remove(remoteOption)
                        holder.updateSelection(remoteOption)
                        mItemClickListener.onItemClick(firstOptionToRemove, false)
                        notifyItemChanged(currentTypeOptions.indexOf(remoteOption))
                    }
                }
                // 리스트에 추가
                selectedOptions.add(selectedOption)
            } else { // 중복 선택 처리 (필터 화면)
                if (selectedOptions.contains(selectedOption)) {
                    selectedOptions.remove(selectedOption)
                } else {
                    selectedOptions.add(selectedOption)
                }
            }
            mItemClickListener.onItemClick(optionList[position], selectedOptions.contains(selectedOption))
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