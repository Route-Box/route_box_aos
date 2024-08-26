package com.example.routebox.presentation.ui.route.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemActivityPictureAlbumBinding
import com.example.routebox.databinding.ItemActivityPictureCameraBinding
import com.example.routebox.domain.model.ActivityPictureAlbum
import com.example.routebox.domain.model.loadingType
import com.example.routebox.domain.model.pictureAddType
import com.example.routebox.domain.model.pictureImgType
import com.example.routebox.domain.model.routeType
import kotlinx.coroutines.launch

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
                val binding: ItemActivityPictureCameraBinding = ItemActivityPictureCameraBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
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
                // TODO: 카메라 연결
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

    inner class CameraViewHolder(val binding: ItemActivityPictureCameraBinding): RecyclerView.ViewHolder(binding.root) {
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