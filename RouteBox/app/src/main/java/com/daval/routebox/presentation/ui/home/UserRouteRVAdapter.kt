package com.daval.routebox.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemUserRouteBinding
import com.daval.routebox.domain.model.UserRoute

class UserRouteRVAdapter(
    private var routeList: ArrayList<UserRoute>
): RecyclerView.Adapter<UserRouteRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: RouteItemClickListener

    fun setRouteClickListener(itemClickListener: RouteItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface RouteItemClickListener {
        fun onItemClick(routeId: Int)
        fun onOptionClick(routeId: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemUserRouteBinding = ItemUserRouteBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = routeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routeList[position])
        holder.apply {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(routeList[position].routeId)
            }
            binding.optionIv.setOnClickListener {
                mItemClickListener.onOptionClick(routeList[position].routeId)
            }
        }
    }

    inner class ViewHolder(val binding: ItemUserRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(route: UserRoute) {
            binding.route = route
        }
    }
}
