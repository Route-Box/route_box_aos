package com.example.routebox.presentation.ui.route.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemActivityAddPictureBinding
import com.example.routebox.databinding.ItemActivityPictureAlbumBinding
import com.example.routebox.databinding.ItemActivityPictureBinding
import com.example.routebox.domain.model.pictureImgType
import com.example.routebox.domain.model.pictureAddType

class PictureRVAdapter(
    private var imgList: ArrayList<Uri?>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var mItemClickListener: MyItemClickListener

    fun setPictureClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onPlusItemClick(position: Int)
        fun onPictureItemClick(position: Int)
    }

    // 뷰의 타입을 정해주는 부분
    override fun getItemViewType(position: Int): Int {
        return when (imgList[position]) {
            null -> pictureAddType
            else -> pictureImgType
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            pictureAddType -> {
                val binding: ItemActivityAddPictureBinding = ItemActivityAddPictureBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return AddPictureViewHolder(binding)
            }
            else -> {
                val binding: ItemActivityPictureBinding = ItemActivityPictureBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return PictureViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (imgList[position] == null) {
            (holder as AddPictureViewHolder).bind()
            holder.setIsRecyclable(false)
            holder.itemView.setOnClickListener {
                mItemClickListener.onPlusItemClick(position)
            }
        } else {
            (holder as PictureViewHolder).bind(imgList[position]!!)
            holder.setIsRecyclable(false)
            holder.binding.deleteIv.setOnClickListener {
                mItemClickListener.onPictureItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = imgList.size

    inner class AddPictureViewHolder(val binding: ItemActivityAddPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() { }
    }

    inner class PictureViewHolder(val binding: ItemActivityPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(img: Uri) {
            binding.img = img
        }
    }

    fun addItem(img: Uri) {
        imgList.add(img)
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imgList.removeAt(position)
        this.notifyDataSetChanged()
    }
}