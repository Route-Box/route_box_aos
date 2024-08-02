package com.example.routebox.presentation.ui.seek.comment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemCommentBinding

class CommentRVAdapter: RecyclerView.Adapter<CommentRVAdapter.ViewHolder>(){

    private var commentList = ArrayList<String>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setCommentClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addComment(commentList: ArrayList<String>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun onMoreButtonClick(view: View?, position: Int, isMine: Boolean)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommentBinding = ItemCommentBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        Log.d("CommentRVAdapter", "")

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position])
        holder.apply {
            binding.itemCommentMoreIv.setOnClickListener {
                mItemClickListener.onMoreButtonClick(binding.itemCommentMoreIv, position, true)
            }
        }
    }

    override fun getItemCount(): Int = commentList.size

    inner class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        //TODO: 실제 Comment 데이터로 변경
        fun bind(commentContent: String) {
            binding.commentContent = commentContent
        }
    }
}