package com.daval.routebox.presentation.ui.user.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemPictureBinding
import com.daval.routebox.databinding.ItemPicturePlusBinding
import com.daval.routebox.domain.model.pictureImgType
import com.daval.routebox.domain.model.pictureAddType

class InquiryPictureRVAdapter(
    private var imgList: ArrayList<String?>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var mItemClickListener: MyItemClickListener

    fun setPictureClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onPlusItemClick(position: Int)
        fun onPictureDeleteIconClick(position: Int)
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
                val binding: ItemPicturePlusBinding = ItemPicturePlusBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return PlusPictureViewHolder(binding)
            }
            else -> {
                val binding: ItemPictureBinding = ItemPictureBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return PictureViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (imgList[position] == null) {
            (holder as PlusPictureViewHolder).bind()
            holder.setIsRecyclable(false)
            holder.itemView.setOnClickListener {
                mItemClickListener.onPlusItemClick(position)
            }
        } else {
            (holder as PictureViewHolder).bind(imgList[position]!!)
            holder.setIsRecyclable(false)
            holder.binding.deleteIv.setOnClickListener {
                mItemClickListener.onPictureDeleteIconClick(position)
            }
        }
    }

    override fun getItemCount(): Int = imgList.size

    inner class PlusPictureViewHolder(val binding: ItemPicturePlusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() { }
    }

    inner class PictureViewHolder(val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(img: String) {
            binding.img = Uri.parse(img)
        }
    }

    fun addItem(img: String) {
        imgList.add(img)
        this.notifyDataSetChanged()
    }

    fun addAllItems(imgList: ArrayList<String>) {
        this.imgList = arrayListOf(null)
        for (i in 0 until imgList.size) {
            this.imgList.add(imgList[i])
        }
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imgList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun returnAllItems(): ArrayList<String?> {
        return imgList
    }
}