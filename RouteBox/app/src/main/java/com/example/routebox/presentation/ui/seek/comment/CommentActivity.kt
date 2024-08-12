package com.example.routebox.presentation.ui.seek.comment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityCommentBinding
import com.example.routebox.presentation.ui.seek.comment.adapter.CommentRVAdapter

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

        initComment()
        initClickListeners()
        setAdapter()
        initObserve()
    }

    private fun initComment() {
        intent.getStringExtra("comment")?.let { viewModel.initComment(it) }
    }

    private fun initClickListeners() {
        // 뒤로가기
        binding.commentBackIv.setOnClickListener {
            finish()
        }
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
            override fun onMoreButtonClick(view: View?, position: Int, isMine: Boolean) {
                //TODO: 내 댓글이라면 수정/삭제하기 메뉴 노출, 아니라면 신고하기 메뉴 노출
                if (position % 2 == 0) myMenuShow(view!!)
                else reportMenuShow(view!!)
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

    private fun myMenuShow(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.my_option_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    Toast.makeText(this, "수정하기 버튼 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_delete -> {
                    Toast.makeText(this, "삭제하기 버튼 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun reportMenuShow(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.report_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    Toast.makeText(this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}