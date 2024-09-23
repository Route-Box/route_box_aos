package com.example.routebox.presentation.ui.route

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteDetailBinding
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.DialogType
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditBaseActivity
import com.example.routebox.presentation.ui.seek.adapter.RouteTagRVAdapter
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteDetailActivity : AppCompatActivity(), PopupDialogInterface {
    private lateinit var binding: ActivityRouteDetailBinding

    private val viewModel: RouteViewModel by viewModels()
    private lateinit var tagAdapter: RouteTagRVAdapter
    private lateinit var activityAdapter: ActivityRVAdapter
    private lateinit var kakaoMap: KakaoMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_detail)

        binding.apply {
            route = RouteDetail()
            lifecycleOwner = this@RouteDetailActivity
        }

        initMapSetting()
        initClickListeners()
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        initRoute()
    }

    private fun initMapSetting() {
        binding.kakaoMap.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }
            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.d("KakaoMap", "onMapError: $error")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                Log.d("KakaoMap", "onMapReady: $kakaoMap")
                this@RouteDetailActivity.kakaoMap = kakaoMap
            }
        })
    }

    private fun initRoute() {
        // 내 루트 상세조회 API 호출
        val routeId = intent.getIntExtra("routeId", 0)
        viewModel.tryGetMyRouteDetail(routeId)
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
            intent.putExtra("comment", viewModel.route.value!!.routeName)
            startActivity(intent)
        }
    }

    private fun setTagAdapter() {
        tagAdapter = RouteTagRVAdapter(viewModel.tagList.value!!)
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
        activityAdapter.addAllActivities(viewModel.route.value!!.routeActivities as MutableList<ActivityResult>)
    }

    private fun initObserve() {
        viewModel.route.observe(this) { route ->
            if (route.routeId != -1) {
                binding.route = route
            }

            if (route.routeActivities.size != 0) { // 활동 정보가 있다면
                setActivityAdapter()
            }
        }

        viewModel.tagList.observe(this) { tagList ->
            if (tagList.size != 0) { // 태그 정보가 있다면
                setTagAdapter()
            }
        }

        viewModel.isDeleteRouteSuccess.observe(this) { isSuccess ->
            if (isSuccess) { // 삭제 완료 후 뒤로가기
                finish()
            }
        }
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.route_my_menu)
        // 공개 여부에 따라 메뉴 아이템의 텍스트 변경
        val changeShowMenuItem = popupMenu.menu.findItem(R.id.menu_make_public_or_private)
        if (viewModel.isPublic) {
            changeShowMenuItem.setTitle(R.string.route_my_make_private)
        } else {
            changeShowMenuItem.setTitle(R.string.route_my_make_public)
        }
        // 메뉴 노출
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> { // 수정하기
                    // 루트 수정 화면으로 이동
                    val intent = Intent(this, RouteEditBaseActivity::class.java)
                    intent.apply {
                        putExtra("route", Gson().toJson(viewModel.route.value))
                        putExtra("isEditMode", true)
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_make_public_or_private -> { // 공개/비공개 전환
                    showChangePublicPopupDialog() // 확인 다이얼로그 노출
                    true
                }
                R.id.menu_delete -> { // 삭제하기
                    showDeletePopupDialog() // 확인 다이얼로그 노출
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun showChangePublicPopupDialog() {
        val popupContent = if (viewModel.isPublic) R.string.route_my_change_to_private_popup_content else R.string.route_my_change_to_public_popup_content
        val dialog = CommonPopupDialog(this, DialogType.CHANGE_PUBLIC.id, String.format(resources.getString(popupContent)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(this.supportFragmentManager, "PopupDialog")
    }

    private fun showDeletePopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(this.supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        if (DialogType.getDialogTypeById(id) == DialogType.CHANGE_PUBLIC) { // 공개 여부 전환 확인
            // 공개 상태라면 비공개 전환, 비공개 상태라면 공개 전환
            viewModel.tryChangePublic()
        } else { // 삭제 확인
            viewModel.tryDeleteRoute()
        }
    }
}