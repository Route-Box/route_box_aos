package com.example.routebox.presentation.ui.search.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.routebox.R
import com.example.routebox.databinding.ItemSearchHomeRouteBinding
import com.example.routebox.domain.model.RoutePreview

class SearchHomeRouteRVAdapter(
    private val routeList: ArrayList<RoutePreview>
): RecyclerView.Adapter<SearchHomeRouteRVAdapter.ViewHolder>() {
    
    // 이미지 ViewPager 부분
    private lateinit var imageVPAdapter: RouteImageVPAdapter
    private var imageList = arrayListOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchHomeRouteRVAdapter.ViewHolder {
        val binding: ItemSearchHomeRouteBinding = ItemSearchHomeRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // ViewPager 연결
         initVPAdapter(binding)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHomeRouteRVAdapter.ViewHolder, position: Int) {
        holder.bind(routeList[holder.adapterPosition])
    }

    override fun getItemCount(): Int = routeList.size

    inner class ViewHolder(val binding: ItemSearchHomeRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            binding.preview = data

            imageVPAdapter.removeAll()
            if (data.img != null) {
                imageVPAdapter.addAllItems(data.img)
            }
        }
    }

    private fun initVPAdapter(binding: ItemSearchHomeRouteBinding) {
        imageVPAdapter = RouteImageVPAdapter(imageList)
        binding.imageVp.adapter = imageVPAdapter
        binding.imageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.imageCi.setViewPager(binding.imageVp)
    }
}