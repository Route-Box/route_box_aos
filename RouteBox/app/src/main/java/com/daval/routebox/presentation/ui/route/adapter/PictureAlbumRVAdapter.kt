package com.daval.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemActivityPictureAlbumBinding
import com.daval.routebox.databinding.ItemPictureCameraBinding
import com.daval.routebox.domain.model.ActivityPictureAlbum
import com.daval.routebox.domain.model.pictureAddType
import com.daval.routebox.domain.model.pictureImgType

class PictureAlbumRVAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pictureList: ArrayList<ActivityPictureAlbum> = arrayListOf()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setPictureClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onPictureItemClick(position: Int, data: ActivityPictureAlbum)
        fun onCameraItemClick()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> pictureAddType
            else -> pictureImgType
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            pictureAddType -> {
                val binding: ItemPictureCameraBinding = ItemPictureCameraBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return CameraViewHolder(binding)
            }
            else -> {
                val binding: ItemActivityPictureAlbumBinding = ItemActivityPictureAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return PictureViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as CameraViewHolder).bind()
            holder.setIsRecyclable(false)
            holder.itemView.setOnClickListener { 
                mItemClickListener.onCameraItemClick()
            }
        } else {
            (holder as PictureViewHolder).bind(pictureList[position])
            holder.setIsRecyclable(false)
            holder.itemView.setOnClickListener {
                mItemClickListener.onPictureItemClick(position, pictureList[position])
            }
        }
    }

    override fun getItemCount(): Int = pictureList.size

    inner class CameraViewHolder(val binding: ItemPictureCameraBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() { }
    }

    inner class PictureViewHolder(val binding: ItemActivityPictureAlbumBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(picture: ActivityPictureAlbum) {
            binding.picture = picture
        }
    }

    fun addAllItems(placeItems: ArrayList<ActivityPictureAlbum>) {
//        pictureList.add(ActivityPictureAlbum(null, null))
        pictureList.addAll(placeItems)
        this.notifyDataSetChanged()
    }

    fun resetAllItems(placeItems: ArrayList<ActivityPictureAlbum>) {
        pictureList = placeItems
        this.notifyDataSetChanged()
    }

    fun returnAllItems(): ArrayList<ActivityPictureAlbum> {
        return pictureList
    }

    fun returnItems(position: Int): ActivityPictureAlbum {
        return pictureList[position]
    }

    fun changeStatus(position: Int, selectedStatus: Boolean, selectedNumber: Int?) {
        if (selectedStatus) {
            pictureList[position].selectedNumber = null
        } else pictureList[position].selectedNumber = selectedNumber
        this.notifyItemChanged(position)
    }
}