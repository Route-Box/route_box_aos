package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemActivityImageBinding
import com.daval.routebox.domain.model.ActivityImage

class ActivityImageRVAdapter(private val images: List<ActivityImage>): RecyclerView.Adapter<ActivityImageRVAdapter.ViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemActivityImageBinding = ItemActivityImageBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // images의 사이즈를 넘어가는 경우 null을 전달
        if (position < images.size) {
            holder.bind(images[position])
        } else {
            holder.bind(null)
        }
    }

    override fun getItemCount(): Int = 3

    inner class ViewHolder(val binding: ItemActivityImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityImage: ActivityImage?) {
            binding.activityImage = activityImage
        }
    }
}