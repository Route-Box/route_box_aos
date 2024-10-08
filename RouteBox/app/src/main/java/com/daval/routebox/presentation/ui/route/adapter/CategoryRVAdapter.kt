package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.R
import com.daval.routebox.databinding.ItemCategoryBinding
import com.daval.routebox.domain.model.Category

class CategoryRVAdapter: RecyclerView.Adapter<CategoryRVAdapter.ViewHolder>(){

    private lateinit var mItemClickListener: MyItemClickListener
    private var selectedIndex: Int = -1
    private var preSelectedIndex: Int = -1
    private var categoryList = Category.getAllCategories()

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

    fun setSelectedName(category: String) {
        selectedIndex = categoryList.lastIndex // ETC 카테고리 id 미리 세팅 (categoryList에서 찾아지지 않을 경우 방지)
        for (i in categoryList.indices) {
            if (categoryList[i].categoryName == category) {
                selectedIndex = i
            }
        }
        this.notifyItemChanged(selectedIndex)

        preSelectedIndex = selectedIndex
    }
}