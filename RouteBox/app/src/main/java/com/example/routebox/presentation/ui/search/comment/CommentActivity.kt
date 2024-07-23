package com.example.routebox.presentation.ui.search.comment

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityCommentBinding

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding

    private lateinit var commentAdapter: CommentRVAdapter

    private val viewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)

        binding.apply {
            viewModel = this@CommentActivity.viewModel
            lifecycleOwner = this@CommentActivity
        }

        setAdapter()
        initObserve()
    }

    private fun setAdapter() {
        Log.d("CommentActivity", "setAdapter()")
        commentAdapter = CommentRVAdapter()
        binding.commentRv.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(context)
        }
        commentAdapter.setCommentClickListener(object: CommentRVAdapter.MyItemClickListener {
            // 아이템 클릭
            override fun onMoreButtonClick(position: Int) {
                //TODO: 신고하기 or 수정/삭제하기의 팝업 메뉴 띄우기
            }
        })
    }

    private fun initObserve() {
        // commentList 관찰하여 리사이클러뷰 아이템에 추가
        viewModel.commentList.observe(this) { commentList ->
            Log.d("CommentActivity", "commentList: $commentList")
            if (!commentList.isNullOrEmpty()) {
                commentAdapter.addComment(commentList)
            }
        }
    }
}