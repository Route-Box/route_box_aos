package com.example.routebox.presentation.ui.search.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.routebox.databinding.ItemImageBinding

class RouteImageVPAdapter(
    private val imageList: ArrayList<String>
): RecyclerView.Adapter<RouteImageVPAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteImageVPAdapter.ViewHolder {
        val binding: ItemImageBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteImageVPAdapter.ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    inner class ViewHolder(val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            Glide.with(binding.root.context).load(data).into(binding.routeImage)
            Log.d("VP-TEST", "data = ${data}")
//            binding.routeImage.setImageURI(Uri.parse(data))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllItems(items: ArrayList<String>) {
        imageList.addAll(items)
        Log.d("VP-TEST", "imageList = $imageList")
        this.notifyDataSetChanged()
    }

    fun removeAll() {
        imageList.clear()
        this.notifyDataSetChanged()
    }
}