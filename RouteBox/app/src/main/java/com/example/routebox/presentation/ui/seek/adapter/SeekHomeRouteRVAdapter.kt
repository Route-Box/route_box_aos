package com.example.routebox.presentation.ui.seek.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.routebox.R
import com.example.routebox.databinding.ItemLoadingBinding
import com.example.routebox.databinding.ItemSeekHomeRouteBinding
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.model.loadingType
import com.example.routebox.domain.model.routeType

class SeekHomeRouteRVAdapter(
    private var routeList: ArrayList<RoutePreview>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 뷰의 타입을 정해주는 부분
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (routeList[position].title) {
            null -> loadingType
            else -> routeType
        }
    }
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when(viewType) {
            routeType -> {
                val binding: ItemSeekHomeRouteBinding = ItemSeekHomeRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RouteTypeViewHolder(binding)
            } else -> {
                val binding: ItemLoadingBinding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadingTypeViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (routeList[position].title == null) {
            (holder as LoadingTypeViewHolder).bind(routeList[position])
            holder.setIsRecyclable(false)
        } else {
            (holder as RouteTypeViewHolder).bind(routeList[position])
            holder.setIsRecyclable(false)
        }
    }

    override fun getItemCount(): Int = routeList.size

    inner class RouteTypeViewHolder(val binding: ItemSeekHomeRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            // ViewPager 연결
            if (data.img != null) setVPAdapter(binding, data.img)
            binding.preview = data
        }
    }

    inner class LoadingTypeViewHolder(val binding: ItemLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            Glide.with(binding.root).load(R.drawable.anim_loading).into(binding.loadingIv)
        }
    }

    private fun setVPAdapter(binding: ItemSeekHomeRouteBinding, imageList: ArrayList<String>) {
        val imageVPAdapter = RouteImageVPAdapter(imageList)
        binding.imageVp.adapter = imageVPAdapter
        binding.imageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.imageCi.setViewPager(binding.imageVp)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLoading() {
        var loadingList = arrayListOf(RoutePreview(null, null, null, null, null, null, null, null))
        loadingList.addAll(routeList)
        routeList = loadingList
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteLoading() {
        routeList.removeAt(0)
        this.notifyDataSetChanged()
    }

    fun checkLoading(): Boolean {
        return routeList[0].title == null
    }
}