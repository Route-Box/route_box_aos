package com.example.routebox.presentation.ui.route.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemBankBinding
import com.example.routebox.databinding.ItemCategoryBinding
import com.example.routebox.domain.model.Category

class CategoryRVAdapter: RecyclerView.Adapter<CategoryRVAdapter.ViewHolder>(){

    private lateinit var mItemClickListener: MyItemClickListener
    private lateinit var context: Context
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
        holder.bind(categoryList[position])
        holder.itemView.setOnClickListener {
            // TODO: 색상 나오면 수정
            mItemClickListener.onItemClick(position, false)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.category = category

            binding.categoryIv.setImageResource(category.categoryIcon)
        }
    }

    fun getItem(position: Int): Category {
        return categoryList[position]
    }
}