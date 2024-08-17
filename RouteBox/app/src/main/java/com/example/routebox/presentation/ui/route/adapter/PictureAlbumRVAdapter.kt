package com.example.routebox.presentation.ui.route.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemActivityPictureAlbumBinding
import com.example.routebox.domain.model.ActivityPictureAlbum

class PictureAlbumRVAdapter(
    private var pictureList: ArrayList<ActivityPictureAlbum>
): RecyclerView.Adapter<PictureAlbumRVAdapter.ViewHolder>() {

    private lateinit var mItemClickListener: MyItemClickListener

    fun setPictureClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PictureAlbumRVAdapter.ViewHolder {
        val binding: ItemActivityPictureAlbumBinding = ItemActivityPictureAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(pictureList[position])
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = pictureList.size

    inner class ViewHolder(val binding: ItemActivityPictureAlbumBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(picture: ActivityPictureAlbum) {
            binding.picture = picture
        }
    }

    fun addAllItems(placeItems: ArrayList<ActivityPictureAlbum>) {
        pictureList.addAll(placeItems)
        this.notifyDataSetChanged()
    }

    fun resetAllItems(placeItems: ArrayList<ActivityPictureAlbum>) {
        pictureList = placeItems
        this.notifyDataSetChanged()
    }
}