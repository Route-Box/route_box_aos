package com.example.routebox.presentation.ui.route.write

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.routebox.databinding.FragmentRouteTrackingBinding
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory

@RequiresApi(Build.VERSION_CODES.O)
class RouteTrackingFragment: Fragment() {

    private lateinit var binding: FragmentRouteTrackingBinding
    private val viewModel: RouteEditViewModel by activityViewModels()
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private lateinit var kakaoMap: KakaoMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteTrackingBinding.inflate(layoutInflater, container, false)

        initMapSetting()
        initClickListener()
        initObserve()

        return binding.root
    }

    private fun initMapSetting() {
        binding.trackingMap.start(object : MapLifeCycleCallback() {
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
                this@RouteTrackingFragment.kakaoMap = kakaoMap

                if (viewModel.route.value?.routeActivities != null) {
                    for (i in 0 until viewModel.route.value?.routeActivities!!.size) {
                        addMarker(viewModel.route.value?.routeActivities!![i].longitude, viewModel.route.value?.routeActivities!![i].latitude)
                    }
                }
            }
        })
    }

    private fun addMarker(latitude: String, longitude: String) {
        // Marker 표시 추가
    }

    private fun initClickListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun initObserve() {
        writeViewModel.currentCoordinate.observe(viewLifecycleOwner) {
            if (writeViewModel.currentCoordinate.value != null) {
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(writeViewModel.currentCoordinate.value?.latitude!!.toDouble(), writeViewModel.currentCoordinate.value?.longitude!!.toDouble()))
                kakaoMap.moveCamera(cameraUpdate)
            }
        }
    }

    // 크래시가 발생할 수도 있어 지도의 LifeCycle도 함께 관리 필요!
    override fun onResume() {
        super.onResume()
        binding.trackingMap.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.trackingMap.pause()
    }
}