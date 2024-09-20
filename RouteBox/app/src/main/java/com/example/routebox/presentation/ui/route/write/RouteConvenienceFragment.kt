package com.example.routebox.presentation.ui.route.write

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteConvenienceBinding
import com.example.routebox.domain.model.CategoryGroupCode
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.RoadViewRequest.Marker
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import okio.`-DeprecatedOkio`

/**
 * TODO: 이전 카테고리 마커 삭제
 * TODO: 사진 RecyclerView
 */
@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceFragment: Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: FragmentRouteConvenienceBinding
    private lateinit var kakaoMap: KakaoMap
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private var categoryDotImg: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteConvenienceBinding.inflate(inflater, container, false)

        initMapSetting()
        initClickListener()
        initRadioButton()
        setAdapter()

        return binding.root
    }

    private fun initMapSetting() {
        binding.convenienceMap.start(object : MapLifeCycleCallback() {
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
                this@RouteConvenienceFragment.kakaoMap = kakaoMap

                val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                kakaoMap.moveCamera(cameraUpdate)

                initObserve()
            }

            override fun getZoomLevel(): Int {
                return 17
            }
        })
    }

    private fun initClickListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun initObserve() {
        writeViewModel.currentCoordinate.observe(viewLifecycleOwner) {
            if (writeViewModel.currentCoordinate.value != null) {
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                kakaoMap.moveCamera(cameraUpdate)

                // 현재 위치 마커 띄우기
                var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.ic_gps_marker)))
                val options = LabelOptions.from(LatLng.from(writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude
                )).setStyles(styles)
                val layer = kakaoMap.labelManager!!.layer
                val label = layer!!.addLabel(options)
                label.show()
            }
        }

        writeViewModel.isCategoryEndPage.observe(viewLifecycleOwner) {
            if (writeViewModel.isCategoryEndPage.value == true) {
                for (i in 0 until writeViewModel.placeCategoryResult.value!!.size) {
                    addMarker(writeViewModel.placeCategoryResult.value!![i].y.toDouble(), writeViewModel.placeCategoryResult.value!![i].x.toDouble(), categoryDotImg)
                }
            }
        }
    }
    // 마커 띄우기
    private fun addMarker(latitude: Double, longitude: Double, markerImg: Int) {
        var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(markerImg)))
        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
        val layer = kakaoMap.labelManager!!.layer
        val label = layer!!.addLabel(options)
        label.show()
    }

    // TODO: 누른 카테고리 장소 띄워주기
    private fun initRadioButton() {
        // 선택한 라디오 버튼 글씨 Bold 처리하기 위한 ChangedListener 부분
        binding.categoryStay.setOnCheckedChangeListener(this)
        binding.categoryTour.setOnCheckedChangeListener(this)
        binding.categoryFood.setOnCheckedChangeListener(this)
        binding.categoryCafe.setOnCheckedChangeListener(this)
        binding.categoryCulture.setOnCheckedChangeListener(this)
        binding.categoryParking.setOnCheckedChangeListener(this)

        binding.categoryRadiogroup.setOnCheckedChangeListener { _, buttonId ->
            writeViewModel.setCameraPosition(kakaoMap.cameraPosition!!.position)

            when (buttonId) {
                R.id.category_stay -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.AD5)
                    categoryDotImg = R.drawable.ic_marker_stay

                }
                R.id.category_tour -> {
                    writeViewModel.setTourCategory()
                    categoryDotImg = R.drawable.ic_marker_tour
                }
                R.id.category_food -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.FD6)
                    categoryDotImg = R.drawable.ic_marker_food
                }
                R.id.category_cafe -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.CE7)
                    categoryDotImg = R.drawable.ic_marker_cafe
                }
                R.id.category_culture -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.CT1)
                    categoryDotImg = R.drawable.ic_marker_culture
                }
                R.id.category_parking -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.PK6)
                    categoryDotImg = R.drawable.ic_marker_parking
                }
            }
        }
    }

    private fun removeMarkers() {
    }

    private fun setAdapter() {
        // TODO: 장소 검색 결과 리사이클러뷰 추가
//        binding.placeRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                // 스크롤 최하단 확인
//                if (!binding.placeRv.canScrollVertically(1)) {
//                    if (!viewModel.isKeywordEndPage.value!!) {
//                        viewModel.setPlaceSearchPage(viewModel.placeSearchPage.value!! + 1)
//                        viewModel.pagingPlace()
//                    }
//                }
//            }
//        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        buttonView.typeface = if (isChecked) resources.getFont(R.font.pretendard_bold) else resources.getFont(R.font.pretendard_regular)
    }

    // 크래시가 발생할 수도 있어 지도의 LifeCycle도 함께 관리 필요!
    override fun onResume() {
        super.onResume()
        binding.convenienceMap.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.convenienceMap.pause()
    }
}