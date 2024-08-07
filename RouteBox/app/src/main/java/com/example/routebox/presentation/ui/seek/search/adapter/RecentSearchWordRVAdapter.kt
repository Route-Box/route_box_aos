package com.example.routebox.presentation.ui.seek.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.ItemRecentSearchwordBinding

class RecentSearchWordRVAdapter: RecyclerView.Adapter<RecentSearchWordRVAdapter.ViewHolder>(){

    private var wordList = mutableListOf<String>()
    private lateinit var mItemClickListener: MyItemClickListener
    private lateinit var context: Context

    fun setRecentSearchWordClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSearchWord(searchWordList: List<String>) {
        this.wordList.clear()
        this.wordList.addAll(searchWordList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllWords() {
        this.wordList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteWord(position: Int) {
        if (position >= 0 && position < wordList.size) {
            this.wordList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, wordList.size)
        }
    }

    interface MyItemClickListener {
        fun onItemClick(position: Int, word: String)
        fun onDeleteWord(position: Int, word: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecentSearchwordBinding = ItemRecentSearchwordBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        context = viewGroup.context
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordList[position])
        holder.apply {
            itemView.setOnClickListener {// 최근 검색어 업데이트, 검색어로 재검색
                mItemClickListener.onItemClick(position, wordList[position])
            }
            binding.itemRecentSearchwordDeleteIv.setOnClickListener { // 최근 검색어 삭제
                mItemClickListener.onDeleteWord(position, wordList[position])
                deleteWord(position)
            }
        }
    }

    override fun getItemCount(): Int = wordList.size

    inner class ViewHolder(val binding: ItemRecentSearchwordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: String) {
            binding.searchWord = word
        }
    }
}