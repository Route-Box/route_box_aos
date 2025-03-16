package com.daval.routebox.presentation.ui.seek.comment.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemCommentBinding
import com.daval.routebox.domain.model.Comment

class CommentRVAdapter: RecyclerView.Adapter<CommentRVAdapter.ViewHolder>(){

    private var commentList = listOf<Comment>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setCommentClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateComment(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun onMoreButtonClick(view: View?, commentId: Int, isMine: Boolean)
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
                mItemClickListener.onMoreButtonClick(binding.itemCommentMoreIv, commentList[position].commentId, true)
            }
        }
    }

    override fun getItemCount(): Int = commentList.size

    inner class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(commentContent: Comment) {
            binding.commentContent = commentContent.content
        }
    }
}