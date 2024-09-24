package com.daval.routebox.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRoutePreviewDetailBinding
import com.daval.routebox.presentation.ui.common.report.ReportFeedActivity
import com.daval.routebox.presentation.ui.seek.comment.CommentActivity

class RoutePreviewDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRoutePreviewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_preview_detail)

        initClickListener()
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.commentTv.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            // TODO: 루트 아이디 보내주기
            intent.putExtra("routeId", 0)
            startActivity(intent)
        }

        binding.moreIv.setOnClickListener {
            reportMenuShow(binding.moreIv)
        }
    }

    private fun reportMenuShow(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.report_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    // 신고하기 화면으로 이동
                    //TODO: 루트 id 넘기기
                    startActivity(Intent(this, ReportFeedActivity::class.java)
                        .putExtra("routeId", 0)
                    )
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}