package com.example.routebox.presentation.ui.route.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemConvenienceCategoryResultBinding
import com.example.routebox.domain.model.ConvenienceCategoryResult

class ConveniencePlaceRVAdapter(
    private var placeList: ArrayList<ConvenienceCategoryResult>
): RecyclerView.Adapter<ConveniencePlaceRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConveniencePlaceRVAdapter.ViewHolder {
        val binding: ItemConvenienceCategoryResultBinding = ItemConvenienceCategoryResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConveniencePlaceRVAdapter.ViewHolder, position: Int) {
        holder.bind(placeList[position])
    }

    override fun getItemCount(): Int = placeList.size

    inner class ViewHolder(val binding: ItemConvenienceCategoryResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(place: ConvenienceCategoryResult) {
            binding.placeInfo = place
        }
    }

    fun addAllItems(placeItems: ArrayList<ConvenienceCategoryResult>) {
        placeList = arrayListOf()
        placeList.addAll(placeItems)
        this.notifyDataSetChanged()
    }

    fun resetAllItems(placeItems: ArrayList<ConvenienceCategoryResult>) {
        placeList = placeItems
        this.notifyDataSetChanged()
    }

    fun removeAllItems() {
        placeList = arrayListOf()
        this.notifyDataSetChanged()
    }
}