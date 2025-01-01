package com.daval.routebox.presentation.ui.route.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentRouteEditBinding
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.daval.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.daval.routebox.presentation.utils.MapUtil.DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.utils.MapUtil.getRoutePathCenterPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.O)
class RouteEditFragment : Fragment(), FilterOptionClickListener, OnMapReadyCallback {

    private lateinit var binding: FragmentRouteEditBinding
    private val viewModel: RouteEditViewModel by activityViewModels()
    private lateinit var routeStyleFragment: RouteStyleFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteEditFragment.viewModel
            lifecycleOwner = this@RouteEditFragment
        }

        initMapSetting()
        setInit()
        initClickListeners()
        initObserve()

        return binding.root
    }

    private fun initMapSetting() {
        // 맵 프래그먼트 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.route_edit_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setInit() {
        if (viewModel.isEditMode) { // 루트 수정
            setRouteStyleFragment()
            viewModel.setStepId(1)
            return
        }
        viewModel.setStepId(0) // 루트 마무리
    }

    private fun initClickListeners() {
        // 활동 수정 버튼
        binding.routeEditActivityEditBtn.setOnClickListener {
            // 활동 수정 화면으로 이동
            findNavController().navigate(R.id.action_routeEditFragment_to_routeEditActivityFragment)
        }
        // 완료 버튼
        binding.routeEditDoneBtn.setOnClickListener {
            viewModel.tryEditRoute() // 루트 제목, 내용 저장 진행
        }
    }

    private fun setRouteStyleFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(
            this,
            isFilterScreen = false,
            FilterOption.findOptionsByNames(viewModel.tagList)
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_route_style_frm, routeStyleFragment)
            .commit()
    }

    private fun setMapCenterPoint() {
        val centerLatLng = getRoutePathCenterPoint(viewModel.getActivityList())
        // 카메라 위치 설정 및 줌 레벨 조정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, DEFAULT_ZOOM_LEVEL))
    }

    private fun setActivityMarker() {
        if (!viewModel.hasActivity()) return
        //TODO: 활동 마커 추가하기
    }

    private fun drawRoutePath() {
        if (!viewModel.hasActivity()) return
        //TODO: 이동 경로 선으로 연결
    }

    // 마커 띄우기
    private fun addMarker(latLng: LatLng, category: Category, activityNumber: Int) {
        //TODO: 지도에 마커 추가
    }

    private fun initObserve() {
        viewModel.routeTitle.observe(viewLifecycleOwner) {
            viewModel.checkButtonEnable()
        }

        viewModel.routeContent.observe(viewLifecycleOwner) {
            viewModel.checkButtonEnable()
        }

        viewModel.isEditSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                requireActivity().finish() // 저장이 성공했다면 닫기
            }
        }
    }

    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setMapCenterPoint() // 지도 중심 좌표 설정
    }
}