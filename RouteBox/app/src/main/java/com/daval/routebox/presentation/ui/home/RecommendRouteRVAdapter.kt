package com.daval.routebox.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemHomeRecommendBinding
import com.daval.routebox.domain.model.RecommendRoute

class RecommendRouteRVAdapter(
    private var routeList: ArrayList<RecommendRoute>
): RecyclerView.Adapter<RecommendRouteRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: RouteItemClickListener

    fun setRouteClickListener(itemClickListener: RouteItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface RouteItemClickListener {
        fun onItemClick(routeId: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHomeRecommendBinding = ItemHomeRecommendBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = routeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routeList[position])
        holder.apply {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(routeList[position].id)
            }
        }
    }

    inner class ViewHolder(val binding: ItemHomeRecommendBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(route: RecommendRoute) {
            binding.route = route
        }
    }
}
