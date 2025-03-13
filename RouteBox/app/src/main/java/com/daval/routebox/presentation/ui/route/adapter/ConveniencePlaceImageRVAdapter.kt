package com.daval.routebox.presentation.ui.route.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemConveniencePlaceImageBinding

class ConveniencePlaceImageRVAdapter: RecyclerView.Adapter<ConveniencePlaceImageRVAdapter.ViewHolder>() {

    var imageList: List<Bitmap> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConveniencePlaceImageRVAdapter.ViewHolder {
        val binding: ItemConveniencePlaceImageBinding = ItemConveniencePlaceImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConveniencePlaceImageRVAdapter.ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    fun updateImageList(newList: List<Bitmap>) {
        imageList = newList
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemConveniencePlaceImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bitmap: Bitmap) {
            binding.pictureIv.setImageBitmap(bitmap)
        }
    }
}