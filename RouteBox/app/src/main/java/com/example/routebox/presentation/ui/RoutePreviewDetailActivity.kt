package com.example.routebox.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRoutePreviewDetailBinding
import com.example.routebox.presentation.ui.seek.comment.CommentActivity

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
                    Toast.makeText(this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}