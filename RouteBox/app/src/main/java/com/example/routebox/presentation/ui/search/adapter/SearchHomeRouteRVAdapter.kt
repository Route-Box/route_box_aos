package com.example.routebox.presentation.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.routebox.databinding.ItemSearchHomeRouteBinding
import com.example.routebox.domain.model.RoutePreview

class SearchHomeRouteRVAdapter(
    private val routeList: ArrayList<RoutePreview>
): RecyclerView.Adapter<SearchHomeRouteRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchHomeRouteRVAdapter.ViewHolder {
        val binding: ItemSearchHomeRouteBinding = ItemSearchHomeRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHomeRouteRVAdapter.ViewHolder, position: Int) {
        holder.bind(routeList[holder.adapterPosition])
    }

    override fun getItemCount(): Int = routeList.size

    inner class ViewHolder(val binding: ItemSearchHomeRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            // ViewPager 연결
            if (data.img != null) setVPAdapter(binding, data.img)
            binding.preview = data
        }
    }

    private fun setVPAdapter(binding: ItemSearchHomeRouteBinding, imageList: ArrayList<String>) {
        val imageVPAdapter = RouteImageVPAdapter(imageList)
        binding.imageVp.adapter = imageVPAdapter
        binding.imageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.imageCi.setViewPager(binding.imageVp)
    }
}