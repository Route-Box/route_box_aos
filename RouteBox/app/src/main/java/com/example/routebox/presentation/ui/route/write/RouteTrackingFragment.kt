package com.example.routebox.presentation.ui.route.write

import android.content.Context
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
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteTrackingBinding
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.example.routebox.presentation.utils.SharedPreferencesHelper
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles


@RequiresApi(Build.VERSION_CODES.O)
class RouteTrackingFragment: Fragment() {

    private lateinit var binding: FragmentRouteTrackingBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val viewModel: RouteEditViewModel by activityViewModels()
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private lateinit var kakaoMap: KakaoMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteTrackingBinding.inflate(layoutInflater, container, false)

        initSharedPreferences()
        addBackgroundDots()
        initMapSetting()
        initClickListener()

        return binding.root
    }

    private fun initSharedPreferences() {
        var sharedPreferences = requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        sharedPreferencesHelper.setIsBackground(false)
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
                        addMarker(
                            viewModel.route.value?.routeActivities!![i].latitude.toDouble(),
                            viewModel.route.value?.routeActivities!![i].longitude.toDouble(),
                            R.drawable.ic_marker
                        )
                    }
                }

                initObserve()
                addBackgroundDots()
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

                addMarker(writeViewModel.currentCoordinate.value!!.latitude, writeViewModel.currentCoordinate.value!!.longitude, R.drawable.ic_gps_marker)
            }
        }
    }

    private fun addBackgroundDots() {
        if (sharedPreferencesHelper.getBackgroundCoordinate() != null) {
            val backgroundDots = sharedPreferencesHelper.getBackgroundCoordinate()
            for (i in 0 until backgroundDots!!.size) {
                writeViewModel.addDot(backgroundDots[i]!!.latitude, backgroundDots[i]!!.longitude)
            }
            sharedPreferencesHelper.setBackgroundCoordinate(arrayListOf())
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
        sharedPreferencesHelper.setIsBackground(true)
    }

    // 마커 띄우기
    private fun addMarker(latitude: Double, longitude: Double, markerImg: Int) {
        var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(markerImg)))
        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
        val layer = kakaoMap.labelManager!!.layer
        val label = layer!!.addLabel(options)
        label.show()
    }
}