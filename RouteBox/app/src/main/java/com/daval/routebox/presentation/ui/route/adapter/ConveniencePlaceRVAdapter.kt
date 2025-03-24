package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemConvenienceCategoryResultBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult

class ConveniencePlaceRVAdapter(
    private var placeList: ArrayList<ConvenienceCategoryResult>
): RecyclerView.Adapter<ConveniencePlaceRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: MyItemClickListener

    interface MyItemClickListener {
        fun onItemClick(placeInfo: ConvenienceCategoryResult)
    }

    fun setItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConveniencePlaceRVAdapter.ViewHolder {
        val binding: ItemConvenienceCategoryResultBinding = ItemConvenienceCategoryResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConveniencePlaceRVAdapter.ViewHolder, position: Int) {
        holder.bind(placeList[position])
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(placeList[position])
        }
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