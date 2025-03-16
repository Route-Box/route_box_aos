package com.daval.routebox.presentation.ui.common.routePreview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRoutePreviewDetailBinding
import com.daval.routebox.presentation.ui.common.report.ReportFeedActivity
import com.daval.routebox.presentation.ui.seek.adapter.RouteImageVPAdapter
import com.daval.routebox.presentation.ui.seek.adapter.RouteTagRVAdapter
import com.daval.routebox.presentation.ui.seek.comment.CommentActivity
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutePreviewDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRoutePreviewDetailBinding
    private lateinit var tagAdapter: RouteTagRVAdapter

    private val viewModel: RoutePreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_preview_detail)

        binding.lifecycleOwner = this

        setInit()
        initClickListener()
        initObserve()
    }

    private fun setInit() {
        viewModel.routeId = intent.getIntExtra("routeId", 0)
        viewModel.getRoutePreviewData() // 루트 조회
    }

    private fun initClickListener() {
        // 뒤로가기
        binding.icBack.setOnClickListener {
            finish()
        }

        // 댓글
        binding.commentTv.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("routeId", viewModel.routeId)
                .putExtra("routeId", viewModel.routePreviewDetail.value!!.routeName)
            startActivity(intent)
        }

        // 더보기
        binding.moreIv.setOnClickListener {
            reportMenuShow(binding.moreIv) // 신고하기 노출
        }

        // 구매하기 버튼
        binding.buyPointBtn.setOnClickListener {
            //TODO: 루트 구매 플로우 진행
        }
    }

    private fun setImageAdapter() {
        if (viewModel.getImageUrlList().isNullOrEmpty()) return
        val imageVPAdapter = RouteImageVPAdapter(viewModel.getImageUrlList()!!, viewModel.routePreviewDetail.value!!.nickname)
        binding.imageVp.apply {
            adapter = imageVPAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        binding.imageCi.setViewPager(binding.imageVp)
    }

    private fun setTagAdapter() {
        if (viewModel.tagList.value.isNullOrEmpty()) return
        tagAdapter = RouteTagRVAdapter(viewModel.tagList.value!!)
        binding.routePreviewTagRv.apply {
            adapter = tagAdapter
            layoutManager = FlexboxLayoutManager(context)
        }
    }

    private fun reportMenuShow(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.report_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    // 신고하기 화면으로 이동
                    startActivity(Intent(this, ReportFeedActivity::class.java)
                        .putExtra("routeId", viewModel.routeId)
                    )
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun initObserve() {
        viewModel.routePreviewDetail.observe(this) { routeData ->
            binding.preview = routeData
            binding.multipleImages = (viewModel.getImageUrlList()!!.size > 1)
            if (routeData.routeId > 0) {
                setImageAdapter()
                setTagAdapter()
            }
        }

        viewModel.tagList.observe(this) { tagList ->
            setTagAdapter()
        }
    }
}