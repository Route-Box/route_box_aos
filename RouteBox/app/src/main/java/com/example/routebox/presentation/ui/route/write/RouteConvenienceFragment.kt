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
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteConvenienceBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles


@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceFragment: Fragment() {

    private lateinit var binding: FragmentRouteConvenienceBinding
    private lateinit var kakaoMap: KakaoMap
    private val writeViewModel: RouteWriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteConvenienceBinding.inflate(inflater, container, false)

        initMapSetting()
        initClickListener()

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

                var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.ic_gps_marker)))
                val options = LabelOptions.from(LatLng.from(writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude
                )).setStyles(styles)
                val layer = kakaoMap.labelManager!!.layer
                val label = layer!!.addLabel(options)
                label.show()
            }
        }
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