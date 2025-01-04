package com.daval.routebox.presentation.ui.route.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetActivityBinding
import com.daval.routebox.databinding.FragmentRouteEditActivityBinding
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.presentation.ui.route.RouteActivityActivity
import com.daval.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.MapUtil
import com.daval.routebox.presentation.utils.MapUtil.DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.utils.MapUtil.getRoutePathCenterPoint
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@RequiresApi(Build.VERSION_CODES.O)
class RouteEditActivityFragment : Fragment(), PopupDialogInterface, OnMapReadyCallback {
    private lateinit var binding: FragmentRouteEditActivityBinding

    private val viewModel: RouteEditViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private var googleMap: GoogleMap? = null

    private val activityAdapter = ActivityRVAdapter(true)

    private var deleteId: Int = -1
    private var deleteActivityIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditActivityBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteEditActivityFragment.viewModel
            lifecycleOwner = this@RouteEditActivityFragment
        }

        initMapSetting()
        setInit()
        setActivityAdapter()
        initClickListeners()
        initObserve()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.tryGetMyRouteDetail() // 루트 상세조회 API 호출
    }

    private fun initMapSetting() {
        // 맵 프래그먼트 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.route_edit_activity_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeEditActivityBottomSheet
        bottomSheetDialog.apply {
            this.viewModel = this@RouteEditActivityFragment.viewModel
            this.lifecycleOwner = this@RouteEditActivityFragment
        }

        viewModel.setStepId(2)
    }

    private fun initClickListeners() {
        // 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            startActivity(Intent(activity, RouteActivityActivity::class.java)
                .putExtra("routeId", viewModel.routeId.value)
            )
        }

        // 활동 아이템 클릭
        activityAdapter.setActivityClickListener(object : ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int, data: ActivityResult) { // 활동 수정
                startActivity(Intent(activity, RouteActivityActivity::class.java).apply {
                    putExtra("routeId", viewModel.routeId.value)
                    putExtra("activity", viewModel.route.value!!.routeActivities[position])
                    putExtra("isEdit", true)
                })
            }

            override fun onDeleteButtonClick(position: Int) { // 활동 삭제
                deleteId = activityAdapter.returnActivityId(position)
                deleteActivityIndex = position
                // 활동 삭제 팝업 띄우기
                showPopupDialog()
            }
        })
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setMapCenterPoint() {
        val centerLatLng = getRoutePathCenterPoint(viewModel.getActivityList())
        // 카메라 위치 설정 및 줌 레벨 조정
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, DEFAULT_ZOOM_LEVEL))
    }

    private fun setActivityMarkers() {
        if (!viewModel.hasActivity()) return
        // 활동 마커 추가하기
        viewModel.route.value?.routeActivities!!.forEachIndexed { index, activity ->
            // 지도에 마커 표시
            val markerIcon = MapUtil.createMarkerBitmap(requireContext(), Category.getCategoryByName(activity.category), index.plus(1))
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
        val polylineOptions = MapUtil.getRoutePathPolylineOptions(requireContext(), viewModel.getActivityList())
        googleMap?.addPolyline(polylineOptions)
    }

    private fun clearMap() {
        googleMap?.clear()
    }

    private fun initObserve() {
        viewModel.route.observe(viewLifecycleOwner) { route ->
            if (route.routeActivities.isNotEmpty()) {
                activityAdapter.addAllActivities(route.routeActivities as MutableList<ActivityResult>)
                // 마커 및 선 업데이트
                clearMap() // 기존 마커 지우기
                setActivityMarkers() // 지도에 활동 마커 추가
                drawRoutePath() // 경로 선으로 잇기
            }
        }
    }

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this@RouteEditActivityFragment, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        activity?.let { dialog.show(it.supportFragmentManager, "PopupDialog") }
    }

    override fun onClickPositiveButton(id: Int) {
        Toast.makeText(requireContext(), "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        viewModel.deleteActivity(deleteId)
        activityAdapter.removeItem(deleteActivityIndex)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setMapCenterPoint() // 지도 중심 좌표 설정
        setActivityMarkers() // 지도에 활동 마커 추가
        drawRoutePath() // 경로 선으로 잇기
    }
}