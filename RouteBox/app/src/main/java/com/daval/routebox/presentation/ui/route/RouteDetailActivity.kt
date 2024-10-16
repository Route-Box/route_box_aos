package com.daval.routebox.presentation.ui.route

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteDetailActivity : AppCompatActivity(), PopupDialogInterface {
    private lateinit var binding: ActivityRouteDetailBinding

    private val viewModel: RouteViewModel by viewModels()
    private lateinit var tagAdapter: RouteTagRVAdapter
    private lateinit var activityAdapter: ActivityRVAdapter
    private var kakaoMap: KakaoMap? = null

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
                setMapCenterPoint()
                setActivityMarker()
                drawRoutePath()
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

    private fun setMapCenterPoint() {
        // 지도의 중심 위치 변경
        kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(viewModel.getRoutePathCenterPoint(), DEFAULT_ZOOM_LEVEL))
    }

    private fun setActivityMarker() {
        if (!viewModel.hasActivity()) return
        // 활동 마커 추가하기
        viewModel.route.value?.routeActivities!!.forEachIndexed { index, activity ->
            // 지도에 마커 표시
            addMarker(
                LatLng.from(activity.latitude.toDouble(), activity.longitude.toDouble()),
                Category.getCategoryByName(activity.category),
                index.plus(1) // 장소 번호는 0번부터 시작
            )
        }
    }

    private fun drawRoutePath() {
        if (!viewModel.hasActivity()) return
        val segment: RouteLineSegment = RouteLineSegment.from(viewModel.getLatLngRoutePath()).setStyles(
            setRoutePathStyle(this)
        )
        val options = RouteLineOptions.from(segment)
        // 지도에 선 표시
        kakaoMap?.routeLineManager?.layer?.addRouteLine(options)?.show()
    }

    // 마커 띄우기
    private fun addMarker(latLng: LatLng, category: Category, activityNumber: Int) {
        val layer = kakaoMap?.labelManager?.layer

        // IconLabel 추가
        val iconLabel = layer?.addLabel(
            getMapActivityIconLabelOptions(latLng, category, activityNumber)
        )

        // TextLabel 추가
        val textLabel = layer?.addLabel(
            getMapActivityNumberLabelOptions(latLng, activityNumber)
        )

        // TextLabel의 위치를 IconLabel 내부로 조정
        if (iconLabel != null && textLabel != null) {
            // IconLabel의 크기를 가정 (예: 60x60 픽셀)
            val iconSize = 60f
            // 텍스트를 아이콘 중심에서 약간 위로 이동
            val offsetY = - iconSize / (2.3)

            // changePixelOffset 메서드를 사용하여 텍스트 라벨의 위치 조정
            textLabel.changePixelOffset(0f, offsetY.toFloat())
        }
    }

    private fun initObserve() {
        viewModel.route.observe(this) { route ->
            if (route.routeId != -1) {
                binding.route = route
            }

            if (route.routeActivities.size != 0) { // 활동 정보가 있다면
                setActivityAdapter() // 어댑터 추가
                setActivityMarker() // 지도에 활동 마커 표시d
                drawRoutePath()
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

    companion object {
        const val DEFAULT_ZOOM_LEVEL = 10 // 루트를 표시하는 기본 줌 레벨

        private const val RANK_INTERVAL = 10 // activity 번호에 따른 rank 차이 (더 높은 activityNumber를 가졌다면 핀을 더 위에 표시)
        private const val RANK_OFFSET = 1 // 아이콘-텍스트 간 rank 차이 (기본적으로 텍스트는 아이콘 위에 표시)

        // IconLabel
        private fun setMapIconLabelStyles(category: Category): LabelStyles {
            return LabelStyles.from(
                LabelStyle.from(category.categoryMarkerIcon)
            )
        }

        fun getMapActivityIconLabelOptions(latLng: LatLng, category: Category, activityNumber: Int): LabelOptions {
            return LabelOptions.from(latLng)
                .setStyles(setMapIconLabelStyles(category))
                .setRank((activityNumber * RANK_INTERVAL).toLong()) // activityNumber가 클수록 높은 rank를 가짐
        }

        // TextLabel
        private fun setMapTextLabelStyle(): LabelStyles {
            return LabelStyles.from(
                LabelStyle.from(LabelTextStyle.from(28, Color.WHITE))
            )
        }

        fun getMapActivityNumberLabelOptions(latLng: LatLng, activityNumber: Int): LabelOptions {
            return LabelOptions.from(latLng)
                .setStyles(setMapTextLabelStyle())
                .setTexts(LabelTextBuilder().setTexts(activityNumber.toString()))
                .setRank((activityNumber * RANK_INTERVAL + RANK_OFFSET).toLong()) // 텍스트는 아이콘보다 높은 rank를 가짐
        }

        fun setRoutePathStyle(context: Context): RouteLineStyles {
            return RouteLineStyles.from(
                RouteLineStyle.from(6f, ContextCompat.getColor(context, R.color.main))
            )
        }
    }
}