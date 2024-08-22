package com.example.routebox.presentation.ui.route.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.R
import com.example.routebox.databinding.ItemBankBinding
import com.example.routebox.databinding.ItemCategoryBinding
import com.example.routebox.domain.model.Category

class CategoryRVAdapter: RecyclerView.Adapter<CategoryRVAdapter.ViewHolder>(){

    private lateinit var mItemClickListener: MyItemClickListener
    private var selectedIndex: Int = -1
    private var preSelectedIndex: Int = -1
    private var categoryList = arrayListOf(
        Category.STAY, Category.TOUR, Category.FOOD, Category.CAFE, Category.SNS
        , Category.CULTURE, Category.TOILET, Category.PARKING, Category.ETC
    )

    fun setCategoryClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int, isSelected: Boolean)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCategoryBinding = ItemCategoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            // TODO: 색상 나오면 수정
            mItemClickListener.onItemClick(position, false)
        }
        holder.bind(position, categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, category: Category) {
            if (position == selectedIndex) {
                binding.categoryCv.strokeWidth = 0
                binding.categoryCv.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.main))
                binding.categoryTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            } else {
                binding.categoryCv.strokeWidth = 1
                binding.categoryCv.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.categoryTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.title_black))
            }

            binding.category = category
            binding.categoryIv.setImageResource(category.categoryIcon)
        }
    }

    fun getItem(position: Int): Category {
        return categoryList[position]
    }

    fun setSelectedIndex(position: Int) {
        if (preSelectedIndex != -1) {
            this.notifyItemChanged(preSelectedIndex)
        }

        selectedIndex = position
        this.notifyItemChanged(position)

        preSelectedIndex = selectedIndex
    }
}