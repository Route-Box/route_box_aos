package com.daval.routebox.presentation.ui.route.edit

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.daval.routebox.presentation.utils.MapUtil.getMapActivityIconLabelOptions
import com.daval.routebox.presentation.utils.MapUtil.getMapActivityNumberLabelOptions
import com.daval.routebox.presentation.utils.MapUtil.TEXT_OFFSET_Y
import com.daval.routebox.presentation.utils.MapUtil.getLatLngRoutePath
import com.daval.routebox.presentation.utils.MapUtil.getRoutePathCenterPoint
import com.daval.routebox.presentation.utils.MapUtil.setRoutePathStyle
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment

@RequiresApi(Build.VERSION_CODES.O)
class RouteEditFragment : Fragment(), FilterOptionClickListener {

    private lateinit var binding: FragmentRouteEditBinding
    private val viewModel: RouteEditViewModel by activityViewModels()
    private var kakaoMap: KakaoMap? = null
    private lateinit var routeStyleFragment: RouteStyleFragment

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
                this@RouteEditFragment.kakaoMap = kakaoMap
                setMapCenterPoint()
                setActivityMarker()
                drawRoutePath()
            }
        })
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
        // 지도의 중심 위치 변경
        kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(
            getRoutePathCenterPoint(viewModel.getActivityList()), DEFAULT_ZOOM_LEVEL)
        )
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
        val segment: RouteLineSegment = RouteLineSegment.from(getLatLngRoutePath(viewModel.getActivityList())).setStyles(
            setRoutePathStyle(requireContext())
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
            // changePixelOffset 메서드를 사용하여 텍스트 라벨의 위치 조정
            textLabel.changePixelOffset(0f, TEXT_OFFSET_Y)
        }
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
}