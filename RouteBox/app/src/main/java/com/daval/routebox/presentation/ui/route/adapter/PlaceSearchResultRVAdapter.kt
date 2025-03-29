package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemKakaoSearchPlaceBinding
import com.daval.routebox.domain.model.SearchActivityResult

class PlaceSearchResultRVAdapter(
    private var placeList: ArrayList<SearchActivityResult>
): RecyclerView.Adapter<PlaceSearchResultRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: MyItemClickListener

    fun setPlaceClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onItemClick(place: SearchActivityResult)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceSearchResultRVAdapter.ViewHolder {
        val binding: ItemKakaoSearchPlaceBinding = ItemKakaoSearchPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceSearchResultRVAdapter.ViewHolder, position: Int) {
        holder.bind(placeList[position])
        // 장소 선택 연결
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(placeList[position])
        }
    }

    override fun getItemCount(): Int = placeList.size

    inner class ViewHolder(val binding: ItemKakaoSearchPlaceBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(place: SearchActivityResult) {
            binding.place = place
        }
    }

    fun addAllItems(placeItems: ArrayList<SearchActivityResult>) {
        placeList.addAll(placeItems)
        this.notifyDataSetChanged()
    }

    fun resetAllItems(placeItems: ArrayList<SearchActivityResult>) {
        placeList = placeItems
        this.notifyDataSetChanged()
    }
}