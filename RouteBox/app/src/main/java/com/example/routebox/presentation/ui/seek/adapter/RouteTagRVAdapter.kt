package com.example.routebox.presentation.ui.seek.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemRouteTagBinding

class RouteTagRVAdapter(
    private var tagList: ArrayList<String>
): RecyclerView.Adapter<RouteTagRVAdapter.ViewHolder>() {

    private lateinit var context: Context

    @SuppressLint("NotifyDataSetChanged")
    fun addTag(tagList: ArrayList<String>) {
        this.tagList = tagList
        Log.d("RouteTagRVAdapter", "$tagList")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRouteTagBinding = ItemRouteTagBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        context = viewGroup.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount(): Int = tagList.size

    inner class ViewHolder(val binding: ItemRouteTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.tag = tag
        }
    }
}