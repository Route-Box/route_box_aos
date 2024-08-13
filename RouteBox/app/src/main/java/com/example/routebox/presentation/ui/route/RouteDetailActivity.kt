package com.example.routebox.presentation.ui.route

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteDetailBinding
import com.example.routebox.domain.model.DialogType
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.Route
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditActivity
import com.example.routebox.presentation.ui.seek.adapter.RouteTagRVAdapter
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson

class RouteDetailActivity : AppCompatActivity(), PopupDialogInterface {
    private lateinit var binding: ActivityRouteDetailBinding

    private val viewModel: RouteViewModel by viewModels()
    private lateinit var tagAdapter: RouteTagRVAdapter
    private lateinit var activityAdapter: ActivityRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_detail)

        binding.apply {
            viewModel = this@RouteDetailActivity.viewModel
            lifecycleOwner = this@RouteDetailActivity
        }

        initRoute()
        initClickListeners()
        initObserve()
    }

    private fun initRoute() {
        // intent가 넘어왔는지 확인
        intent.getStringExtra("route")?.let { routeJson ->
            val route = Gson().fromJson(routeJson, Route::class.java) // 값이 넘어왔다면 route 인스턴스에 gson 형태로 받아온 데이터를 넣어줌
            viewModel.setRoute(route)
        }
    }

    private fun initClickListeners() {
        // x 버튼
        binding.routeDetailCloseIv.setOnClickListener {
            finish()
        }
        // 더보기 버튼
        binding.routeDetailMoreIv.setOnClickListener {
            showMenu(it) // 옵션 메뉴 노출
        }
        // 댓글
        binding.routeDetailCommentNumTv.setOnClickListener {
            // 댓글 화면으로 이동
            val intent = Intent(this, CommentActivity::class.java)
            //TODO: 댓글 화면에서 필요한 정보 넘기기 (routeId 등)
            intent.putExtra("comment", viewModel.route.value!!.title)
            startActivity(intent)
        }
    }

    private fun setTagAdapter() {
        tagAdapter = RouteTagRVAdapter(FilterOption.findOptionsByNames(viewModel.route.value!!.tags) as ArrayList<FilterOption>)
        binding.routeDetailTagRv.apply {
            adapter = tagAdapter
            layoutManager = FlexboxLayoutManager(this@RouteDetailActivity)
        }
    }

    private fun setActivityAdapter() {
        activityAdapter = ActivityRVAdapter(false) // 루트 보기 화면에서는 활동 수정 불가
        binding.routeDetailActivityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(this@RouteDetailActivity, LinearLayoutManager.VERTICAL, false)
        }
        activityAdapter.addActivity(viewModel.route.value!!.activities)
    }

    private fun initObserve() {
        viewModel.route.observe(this) { route ->
            if (route.tags.isNotEmpty()) { // 태그 정보가 있다면
                setTagAdapter()
            }

            if (route.activities.isNotEmpty()) { // 활동 정보가 있다면
                setActivityAdapter()
            }
        }
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.route_my_menu)
        // 공개 여부에 따라 메뉴 아이템의 텍스트 변경
        val changeShowMenuItem = popupMenu.menu.findItem(R.id.menu_make_public_or_private)
        if (viewModel.route.value!!.isPrivate) {
            changeShowMenuItem.setTitle(R.string.route_my_make_public)
        } else {
            changeShowMenuItem.setTitle(R.string.route_my_make_private)
        }
        popupMenu.menu.findItem(R.id.menu_delete).setVisible(false) // 삭제하기는 막아둠
        // 메뉴 노출
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    // 루트 수정 화면으로 이동
                    startActivity(
                        Intent(this, RouteEditActivity::class.java)
                            .putExtra("route", Gson().toJson(viewModel.route.value))
                    )
                    true
                }
                R.id.menu_make_public_or_private -> {
                    showPopupDialog()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun showPopupDialog() {
        val popupContent = if (viewModel.route.value!!.isPrivate) R.string.route_my_change_to_public_popup_content else R.string.route_my_change_to_private_popup_content
        val dialog = CommonPopupDialog(this@RouteDetailActivity, DialogType.CHANGE_PUBLIC.id, String.format(resources.getString(popupContent)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(this.supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        //TODO: 공개 상태라면 비공개 전환, 비공개 상태라면 공개 전환
    }
}