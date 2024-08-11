package com.example.routebox.presentation.ui.route.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemRouteMyBinding
import com.example.routebox.domain.model.Route

class MyRouteRVAdapter: RecyclerView.Adapter<MyRouteRVAdapter.ViewHolder>(){

    private var routeList = emptyList<Route>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setRouteClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addRoute(routeList: List<Route>) {
        this.routeList = routeList
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun onMoreButtonClick(view: View?, position: Int, isPrivate: Boolean)
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRouteMyBinding = ItemRouteMyBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routeList[position])
        holder.apply {
            // 아이템 전체 클릭
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(position)
            }
            // 더보기 버튼 클릭
            binding.itemRouteMyMoreIv.setOnClickListener {
                mItemClickListener.onMoreButtonClick(binding.itemRouteMyMoreIv, position, routeList[position].isPrivate)
            }
        }
    }

    override fun getItemCount(): Int = routeList.size

    inner class ViewHolder(val binding: ItemRouteMyBinding) : RecyclerView.ViewHolder(binding.root) {
        //TODO: 실제 Route 데이터로 변경
        fun bind(route: Route) {
            binding.route = route
        }
    }
}