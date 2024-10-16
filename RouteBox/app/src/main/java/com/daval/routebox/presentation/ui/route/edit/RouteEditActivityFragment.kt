package com.daval.routebox.presentation.ui.route.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.daval.routebox.presentation.ui.route.RouteDetailActivity.Companion.DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.ui.route.RouteDetailActivity.Companion.getMapActivityIconLabelOptions
import com.daval.routebox.presentation.ui.route.RouteDetailActivity.Companion.getMapActivityNumberLabelOptions
import com.daval.routebox.presentation.ui.route.RouteDetailActivity.Companion.setRoutePathStyle
import com.daval.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment

@RequiresApi(Build.VERSION_CODES.O)
class RouteEditActivityFragment : Fragment(), PopupDialogInterface {
    private lateinit var binding: FragmentRouteEditActivityBinding

    private val viewModel: RouteEditViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private var kakaoMap: KakaoMap? = null
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
                this@RouteEditActivityFragment.kakaoMap = kakaoMap
                setActivityMarker()
                drawRoutePath()
            }
        })
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

    private fun setActivityMarker() {
        if (!viewModel.hasActivity()) return
        // 활동 마커 추가하기
        viewModel.route.value?.routeActivities!!.forEachIndexed { index, activity ->
            // 지도를 첫 번째 장소로
            if (index == 0) {
                // 지도 위치 조정
                val latLng = LatLng.from(activity.latitude.toDouble(), activity.longitude.toDouble())
                // 카메라를 마커의 위치로 이동
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(latLng, DEFAULT_ZOOM_LEVEL))
            }
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
            // IconLabel의 크기를 가정 (예: 60x60 픽셀)
            val iconSize = 60f
            // 텍스트를 아이콘 중심에서 약간 위로 이동
            val offsetY = - iconSize / (2.3)

            // changePixelOffset 메서드를 사용하여 텍스트 라벨의 위치 조정
            textLabel.changePixelOffset(0f, offsetY.toFloat())
        }
    }

    private fun clearMap() {
        kakaoMap?.labelManager?.layer?.removeAll()
        kakaoMap?.routeLineManager?.layer?.removeAll()
    }

    private fun initObserve() {
        viewModel.route.observe(viewLifecycleOwner) { route ->
            if (route.routeActivities.isNotEmpty()) {
                activityAdapter.addAllActivities(route.routeActivities as MutableList<ActivityResult>)
                // 마커 및 선 업데이트
                clearMap()
                setActivityMarker()
                drawRoutePath()
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
}