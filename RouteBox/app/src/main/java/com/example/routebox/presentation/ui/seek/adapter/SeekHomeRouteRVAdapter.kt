package com.example.routebox.presentation.ui.seek.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.routebox.R
import com.example.routebox.databinding.ItemLoadingBinding
import com.example.routebox.databinding.ItemSeekHomeRouteBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.model.loadingType
import com.example.routebox.domain.model.routeType
import com.google.android.flexbox.FlexboxLayoutManager

class SeekHomeRouteRVAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var routeList = arrayListOf<RoutePreview>()

    private lateinit var mItemClickListener: MyItemClickListener

    fun setRouteCommentClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun moreItemClick(view: View, position: Int)
        fun commentItemClick(position: Int)
    }

    // 뷰의 타입을 정해주는 부분
    override fun getItemViewType(position: Int): Int {
        return when (routeList[position].routeName) {
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
        if (routeList[position].routeName == null) {
            (holder as LoadingTypeViewHolder).bind(routeList[position])
            holder.setIsRecyclable(false)
        } else {
            (holder as RouteTypeViewHolder).bind(routeList[position])
            holder.setIsRecyclable(false)
            holder.apply {
                binding.downloadTv.setOnClickListener {
                    mItemClickListener.commentItemClick(position)
                }
                binding.commentTv.setOnClickListener {
                    mItemClickListener.commentItemClick(position)
                }
                binding.moreIv.setOnClickListener {
                    mItemClickListener.moreItemClick(binding.moreIv, position)
                }
            }
        }
    }

    override fun getItemCount(): Int = routeList.size

    inner class RouteTypeViewHolder(val binding: ItemSeekHomeRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            // ViewPager 연결
            setVPAdapter(binding, data.routeImageUrls, data.routeStyles, data.nickname)
            binding.preview = data
            if (data.routeImageUrls?.size!! > 1) {
                binding.multipleImages = true
                binding.indicator = true
            } else if (data.routeImageUrls?.size!! == 1) {
                binding.multipleImages = true
                binding.indicator = false
            } else {
                binding.multipleImages = false
                binding.indicator = false
            }
        }
    }

    inner class LoadingTypeViewHolder(val binding: ItemLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            Glide.with(binding.root).load(R.drawable.anim_loading).into(binding.loadingIv)
        }
    }

    private fun setVPAdapter(binding: ItemSeekHomeRouteBinding, imageList: ArrayList<String>?, tagList: ArrayList<String>?, nickname: String) {
        if (imageList != null) {
            val imageVPAdapter = RouteImageVPAdapter(imageList, nickname)
            binding.imageVp.adapter = imageVPAdapter
            binding.imageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.imageCi.setViewPager(binding.imageVp)
        }

        if (tagList != null) {
            val tagRVAdapter = RouteTagRVAdapter(tagList)
            binding.optionRv.adapter = tagRVAdapter
            binding.optionRv.layoutManager = FlexboxLayoutManager(binding.root.context)
        }
    }

    fun addItems(items: ArrayList<RoutePreview>) {
        routeList.addAll(items)
        // 깜빡임 문제를 해결하기 위한 임시 코드
        for (i in 0 until items.size) {
            this.notifyItemInserted(routeList.size + i)
        }
    }

    fun returnItems(): ArrayList<RoutePreview> {
        return routeList
    }

    fun resetItems() {
        routeList = arrayListOf()
        this.notifyDataSetChanged()
    }
}