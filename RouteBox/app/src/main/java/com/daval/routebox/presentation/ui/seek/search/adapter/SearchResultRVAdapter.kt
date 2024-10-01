package com.daval.routebox.presentation.ui.seek.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemSearchResultBinding
import com.daval.routebox.domain.model.SearchRoute

class SearchResultRVAdapter: RecyclerView.Adapter<SearchResultRVAdapter.ViewHolder>(){

    private var routeList = ArrayList<SearchRoute>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setRouteClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addRoute(routeList: ArrayList<SearchRoute>) {
        this.routeList = routeList
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSearchResultBinding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routeList[position])
        holder.apply {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = routeList.size

    inner class ViewHolder(val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(route: SearchRoute) {
            binding.route = route
        }
    }
}