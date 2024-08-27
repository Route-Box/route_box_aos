package com.example.routebox.presentation.ui.seek.adapter

import android.annotation.SuppressLint
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

class SeekHomeRouteRVAdapter(
    private var routeList: ArrayList<RoutePreview>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            setVPAdapter(binding, data.routeImageUrls, data.routeStyles)

            binding.preview = data
        }
    }

    inner class LoadingTypeViewHolder(val binding: ItemLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            Glide.with(binding.root).load(R.drawable.anim_loading).into(binding.loadingIv)
        }
    }

    private fun setVPAdapter(binding: ItemSeekHomeRouteBinding, imageList: ArrayList<String>?, tagList: ArrayList<String>?) {
        if (imageList != null) {
            val imageVPAdapter = RouteImageVPAdapter(imageList)
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

    @SuppressLint("NotifyDataSetChanged")
    fun addLoading() {
        var loadingList = arrayListOf(RoutePreview())
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
        return routeList[0].routeName == null
    }
}