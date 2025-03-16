package com.daval.routebox.presentation.ui.seek.comment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityCommentBinding
import com.daval.routebox.presentation.ui.seek.comment.adapter.CommentRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        initData()
        initClickListeners()
        setAdapter()
        initObserve()
    }

    private fun initData() {
        intent.apply {
            getStringExtra("routeName")?.let { viewModel.initTitle(it) }
            getIntExtra("routeId", -1)?.let { viewModel.initRouteId(it) }
        }

        viewModel.getComments()
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
            override fun onMoreButtonClick(view: View?, commentId: Int, isMine: Boolean) {
                if (isMine) myMenuShow(view!!, commentId)
                else reportMenuShow(view!!, commentId)
            }
        })
    }

    private fun initObserve() {
        // commentList 관찰하여 리사이클러뷰 아이템에 추가
        viewModel.commentList.observe(this) { commentList ->
            Log.d("CommentActivity", "commentList: $commentList")
            if (!commentList.isNullOrEmpty()) {
                commentAdapter.updateComment(commentList)
            }
        }
    }

    private fun myMenuShow(view: View, commentId: Int) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.my_option_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    viewModel.setContentForEdit(commentId)
                    true
                }
                R.id.menu_delete -> {
                    viewModel.deleteComment(commentId)
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun reportMenuShow(view: View, commentId: Int) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.report_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    // TODO: 임시
                    Toast.makeText(this, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}