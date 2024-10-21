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
    private fun sameFilterTypeNumberInSelectedOptions(selectedType: FilterType): Int {
        return selectedOptions.count { it.filterType == selectedType }
    }

    private fun canSelectMore(currentSelectedOption: FilterOption): Boolean {
        return sameFilterTypeNumberInSelectedOptions(currentSelectedOption.filterType) < currentSelectedOption.filterType.maxSelectionCount
    }

    // 태그 선택/해제 처리
    private fun handleSelection(currentSelectedOption: FilterOption) {
        if (selectedOptions.contains(currentSelectedOption)) { // 태그 선택 해제
            selectedOptions.remove(currentSelectedOption)
        } else { // 태그 선택
            selectedOptions.add(currentSelectedOption)
        }
    }

    // 필터 유형에서 선택 가능한 최대 태그 개수가 1개인지 판단
    private fun isSingleOption(currentSelectedOption: FilterOption): Boolean {
        return currentSelectedOption.filterType.maxSelectionCount == 1
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
            val currentSelectedOption = optionList[position]
            if (!canDuplicationSelect) { // 단일 선택 처리 (루트 스타일 화면)
                if (!isSingleOption(currentSelectedOption)) { // 여러 개 선택 가능한 유형 (ex. 루트 스타일 - 2개까지 선택 가능)
                    if (!canSelectMore(currentSelectedOption) && !selectedOptions.contains(currentSelectedOption)) return@setOnClickListener // 더이상 선택 불가능
                    handleSelection(currentSelectedOption)
                } else { // 1개만 선택 가능한 유형
                    if (selectedOptions.contains(currentSelectedOption)) return@setOnClickListener // 동일한 아이템 클릭 이벤트는 없음
                    if (!canSelectMore(currentSelectedOption)) { // 선택한 태그 옮겨가기
                        selectedOptions.firstOrNull { it.filterType == currentSelectedOption.filterType }?.also { optionToRemove -> // 현재 선택된 필터 타입과 동일한 항목을 찾음
                            val currentTypeOptions = optionList.filter { it.filterType == currentSelectedOption.filterType } // 동일한 필터 타입의 옵션 리스트 필터링
                            selectedOptions.remove(optionToRemove) // 선택된 옵션 리스트에서 제거
                            holder.updateSelection(optionToRemove) // UI 업데이트 (선택 상태 변경)
                            mItemClickListener.onItemClick(optionToRemove, false) // 클릭 리스너 호출 (선택 해제 동작)
                            notifyItemChanged(currentTypeOptions.indexOf(optionToRemove)) // 해당 항목 UI 변경 알림
                        }
                    }
                    // 리스트에 추가
                    selectedOptions.add(currentSelectedOption)
                }
            } else { // 중복 선택 처리 (필터 화면)
                handleSelection(currentSelectedOption)
            }
            mItemClickListener.onItemClick(optionList[position], selectedOptions.contains(currentSelectedOption))
            holder.updateSelection(currentSelectedOption)
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