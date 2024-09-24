package com.daval.routebox.presentation.ui.seek.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daval.routebox.databinding.ItemRouteImageBinding

class RouteImageVPAdapter(
    private val imageList: ArrayList<String>,
    private val nickname: String
): RecyclerView.Adapter<RouteImageVPAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteImageVPAdapter.ViewHolder {
        val binding: ItemRouteImageBinding = ItemRouteImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteImageVPAdapter.ViewHolder, position: Int) {
        holder.bind(imageList[position], position)
    }

    override fun getItemCount(): Int = imageList.size

    inner class ViewHolder(val binding: ItemRouteImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String, position: Int) {
            Glide.with(binding.root.context).load(data).centerCrop().into(binding.routeImage)

            if (position == imageList.size - 1) {
                binding.lockCl.visibility = View.VISIBLE
                binding.lockCl.setBackgroundColor(Color.parseColor("#99000000"))
            } else {
                binding.lockCl.visibility = View.GONE
                binding.lockCl.setBackgroundColor(Color.parseColor("#99000000"))
            }

            binding.nickname = nickname
        }
    }
}