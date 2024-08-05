package com.example.routebox.presentation.ui.seek

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemFilterOptionBinding
import com.example.routebox.domain.model.FilterOption

class RouteTagRVAdapter(
    private var tagList: ArrayList<FilterOption>
): RecyclerView.Adapter<RouteTagRVAdapter.ViewHolder>() {

    private lateinit var context: Context

    @SuppressLint("NotifyDataSetChanged")
    fun addTag(tagList: ArrayList<FilterOption>) {
        this.tagList = tagList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFilterOptionBinding = ItemFilterOptionBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        context = viewGroup.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount(): Int = tagList.size

    inner class ViewHolder(val binding: ItemFilterOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: FilterOption) {
            binding.option = tag
        }
    }
}