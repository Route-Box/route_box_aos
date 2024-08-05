package com.example.routebox.presentation.ui.seek.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.routebox.R
import com.example.routebox.databinding.ItemLoadingBinding
import com.example.routebox.databinding.ItemSeekHomeRouteBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.model.loadingType
import com.example.routebox.domain.model.routeType
import com.example.routebox.presentation.ui.seek.RouteTagRVAdapter
import com.example.routebox.presentation.ui.seek.search.FilterOptionsRVAdapter
import com.example.routebox.presentation.ui.seek.search.SearchResultRVAdapter.MyItemClickListener
import com.google.android.flexbox.FlexboxLayoutManager

class SeekHomeRouteRVAdapter(
    private var routeList: ArrayList<RoutePreview>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mItemClickListener: MyItemClickListener

    fun setRouteCommentClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int)
    }

    // 뷰의 타입을 정해주는 부분
    override fun getItemViewType(position: Int): Int {
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
            holder.apply {
                binding.commentIv.setOnClickListener {
                    mItemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = routeList.size

    inner class RouteTypeViewHolder(val binding: ItemSeekHomeRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            // ViewPager 연결
            setVPAdapter(binding, data.img, data.tag)

            binding.preview = data
        }
    }

    inner class LoadingTypeViewHolder(val binding: ItemLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoutePreview) {
            Glide.with(binding.root).load(R.drawable.anim_loading).into(binding.loadingIv)
        }
    }

    private fun setVPAdapter(binding: ItemSeekHomeRouteBinding, imageList: ArrayList<String>?, tagList: ArrayList<FilterOption>?) {
        if (imageList != null) {
            val imageVPAdapter = RouteImageVPAdapter(imageList)
            binding.imageVp.adapter = imageVPAdapter
            binding.imageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.imageCi.setViewPager(binding.imageVp)
        }

        if (tagList != null) {
            val tagRVAdapter = RouteTagRVAdapter(tagList)
            binding.optionRv.adapter = tagRVAdapter
            binding.optionRv.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLoading() {
        var loadingList = arrayListOf(RoutePreview(null, null, null, null, null, null, null, null, null))
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