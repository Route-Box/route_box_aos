package com.daval.routebox.presentation.ui.route

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteDetailBinding
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.daval.routebox.presentation.ui.route.edit.RouteEditBaseActivity
import com.daval.routebox.presentation.ui.seek.adapter.RouteTagRVAdapter
import com.daval.routebox.presentation.ui.seek.comment.CommentActivity
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.MapUtil
import com.daval.routebox.presentation.utils.MapUtil.DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.utils.MapUtil.getRoutePathCenterPoint
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteDetailActivity : AppCompatActivity(), PopupDialogInterface, OnMapReadyCallback {
    private lateinit var binding: ActivityRouteDetailBinding

    private val viewModel: RouteViewModel by viewModels()
    private lateinit var tagAdapter: RouteTagRVAdapter
    private lateinit var activityAdapter: ActivityRVAdapter
    private var googleMap: GoogleMap? = null

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
        // 맵 프래그먼트 초기화
        val mapFragment = supportFragmentManager.findFragmentById(R.id.route_detail_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setMapCenterPoint() {
        val centerLatLng = getRoutePathCenterPoint(viewModel.getActivityList())
        // 카메라 위치 설정 및 줌 레벨 조정
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, DEFAULT_ZOOM_LEVEL))
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
            this.layoutManager =
                LinearLayoutManager(this@RouteDetailActivity, LinearLayoutManager.VERTICAL, false)
        }
        activityAdapter.addAllActivities(viewModel.route.value!!.routeActivities as MutableList<ActivityResult>)
    }

    private fun setActivityMarkers() {
        if (!viewModel.hasActivity()) return
        // 활동 마커 추가하기
        viewModel.route.value?.routeActivities!!.forEachIndexed { index, activity ->
            // 지도에 마커 표시
            val markerIcon = MapUtil.createMarkerBitmap(this, Category.getCategoryByName(activity.category), index.plus(1))
            // 마커 추가
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(activity.latitude.toDouble(), activity.longitude.toDouble()))
                    .icon(markerIcon)
                    .zIndex(1f)
            )
        }
    }

    private fun drawRoutePath() {
        if (!viewModel.hasActivity()) return
        // 이동 경로 선으로 연결
        val polylineOptions = MapUtil.getRoutePathPolylineOptions(this, viewModel.getActivityList())
        googleMap?.addPolyline(polylineOptions)
    }

    private fun initObserve() {
        viewModel.route.observe(this) { route ->
            if (route.routeId != -1) {
                binding.route = route
            }

            if (route.routeActivities.size != 0) { // 활동 정보가 있다면
                setMapCenterPoint() // 지도 중심 좌표 변경
                setActivityAdapter() // 어댑터 추가
                setActivityMarkers() // 지도에 활동 마커 추가
                drawRoutePath() // 경로를 선으로 잇기
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
                        putExtra("routeId", viewModel.route.value?.routeId)
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
        val popupContent =
            if (viewModel.isPublic) R.string.route_my_change_to_private_popup_content else R.string.route_my_change_to_public_popup_content
        val dialog = CommonPopupDialog(
            this,
            DialogType.CHANGE_PUBLIC.id,
            String.format(resources.getString(popupContent)),
            null,
            null
        )
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(this.supportFragmentManager, "PopupDialog")
    }

    private fun showDeletePopupDialog() {
        val dialog = CommonPopupDialog(
            this,
            DialogType.DELETE.id,
            String.format(resources.getString(R.string.activity_delete_popup)),
            null,
            null
        )
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setMapCenterPoint() // 지도 중심 좌표 설정
    }
}