package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemRouteMyBinding
import com.daval.routebox.domain.model.MyRoute

class MyRouteRVAdapter :
    ListAdapter<MyRoute, MyRouteRVAdapter.ViewHolder>(DiffCallback()) {

    private lateinit var mItemClickListener: MyItemClickListener

    fun setRouteClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onMoreButtonClick(view: View?, routeId: Int, isPublic: Boolean) // 더보기 버튼 클릭
        fun onCommentButtonClick(position: Int) // 댓글 아이콘 클릭
        fun onItemClick(routeId: Int) // 아이템 전체 클릭
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRouteMyBinding = ItemRouteMyBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = getItem(position)
        holder.bind(route)
        holder.apply {
            // 아이템 전체 클릭
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(route.routeId)
            }
            // 더보기 버튼 클릭
            binding.itemRouteMyMoreIv.setOnClickListener {
                mItemClickListener.onMoreButtonClick(binding.itemRouteMyMoreIv, route.routeId, route.isPublic)
            }
            // 댓글 아이콘 클릭
            binding.itemSearchResultCommentNumTv.setOnClickListener {
                mItemClickListener.onCommentButtonClick(position)
            }
        }
    }

    inner class ViewHolder(val binding: ItemRouteMyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: MyRoute) {
            binding.route = route
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MyRoute>() {
        override fun areItemsTheSame(oldItem: MyRoute, newItem: MyRoute): Boolean {
            return oldItem.routeId == newItem.routeId
        }

        override fun areContentsTheSame(oldItem: MyRoute, newItem: MyRoute): Boolean {
            return oldItem == newItem
        }
    }
}
